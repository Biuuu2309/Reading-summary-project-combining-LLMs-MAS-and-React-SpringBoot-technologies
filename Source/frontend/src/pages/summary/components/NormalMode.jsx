import { useState, useEffect, useRef } from 'react';
import Select from 'react-select';
import { Loader2, AlertCircle, CheckCircle2, ImagePlus, X } from 'lucide-react';
import { submitSummary, parseSummaryResponse, formatNormalModeInput } from '../../../services/summaryService';
import { getApiUserId, getOrCreateSession } from '../../../services/sessionService';
import { handleAPIError } from '../../../services/errorHandler';
import { validateImageFile, fileToBase64, fileToPreviewUrl } from '../../../utils/imageUtils';
import SummaryResult from './SummaryResult';
import { createSummarySession } from '../../../services/summarySessionApi';
import { createFromMas } from '../../../services/summaryHistoryApi';
import './SummaryModes.css';

const summaryOptions = [
  { value: 'abstractive', label: 'Tóm tắt diễn giải' },
  { value: 'extractive', label: 'Tóm tắt trích xuất' },
];

const gradeOptions = [
  { value: '1', label: 'Lớp 1' },
  { value: '2', label: 'Lớp 2' },
  { value: '3', label: 'Lớp 3' },
  { value: '4', label: 'Lớp 4' },
  { value: '5', label: 'Lớp 5' },
];

const lengthOptions = [
  { value: 'short', label: 'Ngắn' },
  { value: 'medium', label: 'Trung bình' },
  { value: 'long', label: 'Dài' },
];

export default function NormalMode({ onSubmit, initialData, summarySessionId, persistHistory = false }) {
  const [summaryType, setSummaryType] = useState(null);
  const [gradeLevel, setGradeLevel] = useState(null);
  const [lengthOption, setLengthOption] = useState(lengthOptions[1]);
  const [text, setText] = useState('');
  const [inputMode, setInputMode] = useState('text');
  const [imagePreview, setImagePreview] = useState(null);
  const [imageBase64, setImageBase64] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);
  const [sessionId, setSessionId] = useState(null);
  const fileInputRef = useRef(null);
  const previewUrlRef = useRef(null);

  // Load initial data if provided
  useEffect(() => {
    if (initialData) {
      setSummaryType(summaryOptions.find(opt => opt.value === initialData.summaryType) || null);
      setGradeLevel(gradeOptions.find(opt => opt.value === initialData.gradeLevel) || null);
      setLengthOption(
        lengthOptions.find(opt => opt.value === initialData.lengthOption) || lengthOptions[1]
      );
      setText(initialData.text || '');
      setInputMode(initialData.inputMode || 'text');
      if (initialData.imagePreview) {
        setImagePreview(initialData.imagePreview);
      }
      if (initialData.result) {
        setResult(initialData.result);
      }
      if (initialData.sessionId) {
        setSessionId(initialData.sessionId);
      }
    }
  }, [initialData]);

  // Reset grade/length when switching to extractive
  useEffect(() => {
    if (summaryType?.value === 'extractive') {
      setGradeLevel(null);
      setLengthOption(lengthOptions[1]);
    }
  }, [summaryType]);

  useEffect(() => {
    return () => {
      if (previewUrlRef.current) {
        URL.revokeObjectURL(previewUrlRef.current);
      }
    };
  }, []);

  const clearImage = () => {
    if (previewUrlRef.current) {
      URL.revokeObjectURL(previewUrlRef.current);
      previewUrlRef.current = null;
    }
    setImagePreview(null);
    setImageBase64(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const applyImageFile = async (file) => {
    const validationError = validateImageFile(file);
    if (validationError) {
      setError(validationError);
      return;
    }

    try {
      const base64 = await fileToBase64(file);
      if (previewUrlRef.current) {
        URL.revokeObjectURL(previewUrlRef.current);
      }
      const previewUrl = fileToPreviewUrl(file);
      previewUrlRef.current = previewUrl;
      setImagePreview(previewUrl);
      setImageBase64(base64);
      setError(null);
    } catch {
      setError('Không đọc được ảnh');
    }
  };

  const handleImageSelect = async (e) => {
    const file = e.target.files?.[0];
    if (file) await applyImageFile(file);
  };

  const handleImagePaste = async (e) => {
    if (inputMode !== 'image') return;
    const items = e.clipboardData?.items;
    if (!items) return;

    for (const item of items) {
      if (item.type.startsWith('image/')) {
        e.preventDefault();
        const file = item.getAsFile();
        if (file) await applyImageFile(file);
        break;
      }
    }
  };

  const handleDrop = async (e) => {
    e.preventDefault();
    if (isLoading || inputMode !== 'image') return;
    const file = e.dataTransfer.files?.[0];
    if (file) await applyImageFile(file);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const hasText = text.trim().length > 0;
    const hasImage = Boolean(imageBase64);
    const inputValid = inputMode === 'text' ? hasText : hasImage;

    if (!summaryType || !inputValid || (summaryType?.value === 'abstractive' && (!gradeLevel || !lengthOption))) {
      return;
    }

    setIsLoading(true);
    setError(null);
    setResult(null);

    try {
      const userId = getApiUserId();
      let currentSummarySessionId = summarySessionId;
      if (persistHistory && !currentSummarySessionId) {
        const created = await createSummarySession({ userId, content: text.trim() });
        currentSummarySessionId = created.sessionId;
        if (onSubmit) {
          onSubmit({ kind: 'session_created', summarySessionId: currentSummarySessionId, source: 'normal_submit' });
        }
      }
      
      // Get or create session
      let currentSessionId = sessionId;
      if (!currentSessionId) {
        currentSessionId = await getOrCreateSession(userId);
        setSessionId(currentSessionId);
      }

      // Submit summary request
      const formData = {
        summaryType: summaryType.value,
        gradeLevel: summaryType.value === 'abstractive' ? gradeLevel?.value : null,
        lengthOption: summaryType.value === 'abstractive' ? lengthOption?.value : null,
        text: inputMode === 'text' ? text.trim() : '',
        inputMode,
        imageBase64: inputMode === 'image' ? imageBase64 : null,
      };

      const response = await submitSummary({
        userId,
        sessionId: currentSessionId,
        conversationId: null,
        formData,
      });

      // Parse response
      const parsedResult = parseSummaryResponse(response);
      setResult(parsedResult);

      if (persistHistory && currentSummarySessionId) {
        const summaryImageUrl = typeof parsedResult.summaryImageUrl === 'string'
          ? parsedResult.summaryImageUrl
          : (parsedResult.summaryImageUrl ? JSON.stringify(parsedResult.summaryImageUrl) : null);
        const evaluation = typeof parsedResult.evaluation === 'string'
          ? parsedResult.evaluation
          : (parsedResult.evaluation ? JSON.stringify(parsedResult.evaluation) : null);
        await createFromMas({
          summarySessionId: currentSummarySessionId,
          userId,
          userInput: formatNormalModeInput(formData),
          summaryContent: parsedResult.summary || '',
          summaryImageUrl,
          evaluation,
          masSessionId: currentSessionId,
          conversationId: null,
        });
      }

      // Call parent onSubmit callback with full data
      if (onSubmit) {
        onSubmit({
          kind: 'normal',
          summaryType: formData.summaryType,
          gradeLevel: formData.gradeLevel,
          lengthOption: formData.lengthOption,
          text: formData.text,
          inputMode: formData.inputMode,
          imagePreview: inputMode === 'image' ? imagePreview : null,
          result: parsedResult,
          sessionId: currentSessionId,
          summarySessionId: persistHistory ? currentSummarySessionId : null,
        });
      }
    } catch (err) {
      const userError = handleAPIError(err);
      setError(userError.message);
      console.error('Submit summary error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const customSelectStyles = {
    control: (provided, state) => ({
      ...provided,
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: state.isFocused ? '#3B82F6' : '#E5E7EB',
      borderRadius: '8px',
      boxShadow: state.isFocused ? '0 0 0 3px rgba(59, 130, 246, 0.1)' : 'none',
      minHeight: '44px',
      '&:hover': {
        borderColor: '#3B82F6',
      },
    }),
    option: (provided, state) => ({
      ...provided,
      backgroundColor: state.isSelected
        ? '#3B82F6'
        : state.isFocused
        ? '#EFF6FF'
        : 'white',
      color: state.isSelected ? 'white' : '#374151',
      '&:hover': {
        backgroundColor: state.isSelected ? '#3B82F6' : '#EFF6FF',
      },
    }),
  };

  const isAbstractive = summaryType?.value === 'abstractive';
  const hasTextInput = text.trim().length > 0;
  const hasImageInput = Boolean(imageBase64);
  const inputValid = inputMode === 'text' ? hasTextInput : hasImageInput;
  const isSubmitDisabled = !summaryType || !inputValid || (isAbstractive && (!gradeLevel || !lengthOption)) || isLoading;

  return (
    <div className="summary-mode-container">
      <form onSubmit={handleSubmit} className="summary-form">
        <div className="form-group">
          <label htmlFor="summary-type">Loại tóm tắt</label>
          <Select
            id="summary-type"
            options={summaryOptions}
            value={summaryType}
            onChange={setSummaryType}
            placeholder="Chọn loại tóm tắt..."
            styles={customSelectStyles}
            isSearchable={false}
            isDisabled={isLoading}
          />
        </div>

        {isAbstractive && (
          <>
            <div className="form-group fade-in">
              <label htmlFor="grade-level">Cấp lớp</label>
              <Select
                id="grade-level"
                options={gradeOptions}
                value={gradeLevel}
                onChange={setGradeLevel}
                placeholder="Chọn cấp lớp..."
                styles={customSelectStyles}
                isSearchable={false}
                isDisabled={isLoading}
              />
            </div>
            <div className="form-group fade-in">
              <label htmlFor="length-option">Độ dài bản tóm tắt</label>
              <Select
                id="length-option"
                options={lengthOptions}
                value={lengthOption}
                onChange={setLengthOption}
                placeholder="Chọn độ dài..."
                styles={customSelectStyles}
                isSearchable={false}
                isDisabled={isLoading}
              />
            </div>
          </>
        )}

        <div className="form-group">
          <label>Đầu vào</label>
          <div className="input-mode-tabs">
            <button
              type="button"
              className={`input-mode-tab ${inputMode === 'text' ? 'active' : ''}`}
              onClick={() => setInputMode('text')}
              disabled={isLoading}
            >
              Văn bản
            </button>
            <button
              type="button"
              className={`input-mode-tab ${inputMode === 'image' ? 'active' : ''}`}
              onClick={() => setInputMode('image')}
              disabled={isLoading}
            >
              Ảnh
            </button>
          </div>
        </div>

        {inputMode === 'text' ? (
          <div className="form-group">
            <label htmlFor="text-input">Văn bản cần tóm tắt</label>
            <textarea
              id="text-input"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Nhập văn bản cần tóm tắt..."
              rows={10}
              className="summary-textarea"
              disabled={isLoading}
            />
          </div>
        ) : (
          <div className="form-group">
            <label>Ảnh cần tóm tắt</label>
            <div
              className={`image-upload-zone ${imagePreview ? 'has-image' : ''}`}
              onPaste={handleImagePaste}
              onDragOver={(e) => e.preventDefault()}
              onDrop={handleDrop}
              onClick={() => !isLoading && fileInputRef.current?.click()}
              role="button"
              tabIndex={0}
              onKeyDown={(e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                  e.preventDefault();
                  fileInputRef.current?.click();
                }
              }}
            >
              <input
                ref={fileInputRef}
                type="file"
                accept="image/jpeg,image/png,image/gif,image/webp,image/bmp"
                onChange={handleImageSelect}
                hidden
                disabled={isLoading}
              />
              {imagePreview ? (
                <div className="image-preview-wrap">
                  <img src={imagePreview} alt="Ảnh đầu vào" className="image-preview" />
                  <button
                    type="button"
                    className="image-remove-btn"
                    onClick={(e) => {
                      e.stopPropagation();
                      clearImage();
                    }}
                    disabled={isLoading}
                    aria-label="Xóa ảnh"
                  >
                    <X size={16} />
                  </button>
                </div>
              ) : (
                <div className="image-upload-placeholder">
                  <ImagePlus size={32} />
                  <p>Nhấn để chọn ảnh, kéo thả hoặc dán (Ctrl+V)</p>
                  <span>JPG, PNG, GIF, WEBP — tối đa 5MB</span>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Error Message */}
        {error && (
          <div className="error-message">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        {/* Success Message */}
        {result && !error && (
          <div className="success-message">
            <CheckCircle2 size={18} />
            <span>Tóm tắt thành công!</span>
          </div>
        )}

        <button 
          type="submit" 
          className="summary-submit-btn" 
          disabled={isSubmitDisabled}
        >
          {isLoading ? (
            <>
              <Loader2 size={18} className="spinning" />
              <span>Đang xử lý...</span>
            </>
          ) : (
            'Tóm tắt'
          )}
        </button>
      </form>

      {/* Result Display */}
      {result && <SummaryResult result={result} />}
    </div>
  );
}
