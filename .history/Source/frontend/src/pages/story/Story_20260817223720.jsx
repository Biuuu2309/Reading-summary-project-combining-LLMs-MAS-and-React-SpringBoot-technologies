import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { BookOpen, BookText, Home as HomeIcon, Search, Zap } from 'lucide-react';
import { MorphingNavigation } from "../../components/lightswind/morphing-navigation.tsx";
import { GravityStarsBackground } from '../../components/animate-ui/components/backgrounds/gravity-stars';
import { getStoredUser } from '../../services/authService';
import './Story.css';
import {
  getSummariesByContributor,
} from '../../services/summaryApi';
import { StarsBackground } from '../../components/animate-ui/components/backgrounds/stars';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';

function formatDate(value) {
  if (!value) return '';
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return '';
  return d.toLocaleString();
}

function resolveImageSrc(src) {
  if (!src) return '';
  if (typeof src !== 'string') return '';
  if (src.startsWith('http://') || src.startsWith('https://')) return src;
  if (src.startsWith('/')) return `${API_BASE_URL}${src}`;
  return `${API_BASE_URL}/${src}`;
}

function parseImageUrls(imageUrl) {
  // imageUrl from SummaryDTO is often a JSON array string (e.g. '["https://...","https://..."]')
  if (!imageUrl) return [];
  try {
    const raw = typeof imageUrl === 'string' ? imageUrl.trim() : imageUrl;
    if (typeof raw === 'string') {
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed)) {
        return parsed
          .map((item) => {
            if (!item) return null;
            if (typeof item === 'string') return item;
            if (typeof item === 'object') return item.url || item.imageUrl || null;
            return null;
          })
          .filter(Boolean);
      }
    }
  } catch {
    // Fallback: treat it as a direct URL/path
  }
  return [typeof imageUrl === 'string' ? imageUrl : ''].filter(Boolean);
}

function parseSummaryImageMapping(summaryImageUrl) {
  // summaryImageUrl from SummaryDTO is a JSON array string of objects:
  // [{"part":"text","url":"https://..."} , ...]
  if (!summaryImageUrl) return [];
  try {
    const raw = typeof summaryImageUrl === 'string' ? summaryImageUrl.trim() : summaryImageUrl;
    const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw;
    if (!Array.isArray(parsed)) return [];
    return parsed
      .map((item) => {
        if (!item || typeof item !== 'object') return null;
        const url = item.url || item.imageUrl || null;
        const text = item.part || item.text || '';
        return url || text ? { imageUrl: url, text } : null;
      })
      .filter(Boolean);
  } catch {
    return [];
  }
}

function SummaryCard({ s, onOpen }) {
  return (
    <button type="button" className="story-card" onClick={() => onOpen(s)} title="Xem chi tiết">
      <div className="story-card-title">{s.title || '(No title)'}</div>
      <div className="story-card-meta">
        <span className="tag">{s.grade || 'N/A'}</span>
        <span className="tag">{s.method || 'N/A'}</span>
        <span className={`tag tag-${(s.status || 'UNKNOWN').toLowerCase()}`}>{s.status || 'UNKNOWN'}</span>
        <span className="muted">Reads: {Number(s.readCount ?? 0)}</span>
      </div>
      <div className="story-card-sub">
        <span className="muted">By: {s.createdByUserId || 'N/A'}</span>
        {s.createdAt ? <span className="muted">{formatDate(s.createdAt)}</span> : null}
      </div>
      <div className="story-card-preview">{s.summaryContent || s.content || ''}</div>
    </button>
  );
}

function filterStories(stories, { searchTerm, searchGrade, status, grade, method }) {
  let result = [...stories];
  if (searchTerm.trim()) {
    const q = searchTerm.trim().toLowerCase();
    result = result.filter((s) => (s.title || '').toLowerCase().includes(q));
  }
  if (searchGrade) {
    result = result.filter((s) => (s.grade || '') === searchGrade);
  }
  if (status) {
    result = result.filter((s) => (s.status || '') === status);
  }
  if (grade) {
    result = result.filter((s) => (s.grade || '') === grade);
  }
  if (method) {
    result = result.filter((s) => (s.method || '') === method);
  }
  return result;
}

function getTopStories(stories, limit = 10) {
  return [...stories]
    .sort((a, b) => Number(b.readCount ?? 0) - Number(a.readCount ?? 0))
    .slice(0, limit);
}

export default function Story() {
  const navigate = useNavigate();
  const [currentUser, setCurrentUser] = useState(() => getStoredUser());

  useEffect(() => {
    const onStorage = () => setCurrentUser(getStoredUser());
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, []);

  const navLinks = useMemo(() => {
    const links = [
      { id: 'home', label: 'Home', href: '/', icon: <HomeIcon size={30} /> },
      { id: 'summary', label: 'Summary', href: '/summary', icon: <BookOpen size={30} /> },
    ];
    if (currentUser?.userId) {
      links.push({ id: 'story', label: 'Story', href: '/story', icon: <BookText size={30} /> });
    }
    links.push({ id: 'mas-flow', label: 'MAS Flow', href: '/mas-flow', icon: <Zap size={30} /> });
    return links;
  }, [currentUser?.userId]);

  const handleLinkClick = useCallback(
    (link) => {
      if (link.id === 'home') navigate('/');
      else if (link.id === 'summary') navigate('/summary');
      else if (link.id === 'story') navigate('/story');
      else if (link.id === 'mas-flow') navigate('/mas-flow');
    },
    [navigate],
  );

  const [top10, setTop10] = useState([]);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [searchTerm, setSearchTerm] = useState('');
  const [searchGrade, setSearchGrade] = useState('');

  const [status, setStatus] = useState('');
  const [grade, setGrade] = useState('');
  const [method, setMethod] = useState('');

  const [selected, setSelected] = useState(null);
  const [myStories, setMyStories] = useState([]);

  const isAuthenticated = Boolean(currentUser?.userId);

  const loadMyStories = useCallback(async () => {
    if (!currentUser?.userId) {
      setMyStories([]);
      setTop10([]);
      setItems([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const res = await getSummariesByContributor(currentUser.userId);
      const list = Array.isArray(res) ? res : [];
      setMyStories(list);
    } catch (e) {
      setError(e?.message || 'Không tải được danh sách story');
      setMyStories([]);
    } finally {
      setLoading(false);
    }
  }, [currentUser?.userId]);

  useEffect(() => {
    loadMyStories();
  }, [loadMyStories]);

  const applyFilters = useCallback(() => {
    if (!isAuthenticated) {
      setItems([]);
      setTop10([]);
      return;
    }
    const filtered = filterStories(myStories, { searchTerm, searchGrade, status, grade, method });
    setItems(filtered);
    setTop10(getTopStories(filtered));
  }, [isAuthenticated, myStories, searchTerm, searchGrade, status, grade, method]);

  useEffect(() => {
    applyFilters();
  }, [applyFilters]);

  const resetAll = useCallback(() => {
    setSearchTerm('');
    setSearchGrade('');
    setStatus('');
    setGrade('');
    setMethod('');
  }, []);

  return (
    <div className="story-page" style={{ width: '100%', minHeight: '100vh', position: 'relative' }}>
      <StarsBackground className="stars-bg"/>
      <MorphingNavigation
        links={navLinks}
        theme="custom"
        backgroundColor="#ffffff00"
        textColor="#0000ff"
        borderColor="rgba(59, 130, 246, 0.9)"
        onLinkClick={handleLinkClick}
        scrollThreshold={150}
        animationDuration={1.5}
        enablePageBlur={true}
        glowIntensity={5}
      />

      <div className="story-wrap">
        <div className="story-header">
          <div>
            <div className="story-title">Story</div>
            <div className="story-subtitle">
              {isAuthenticated
                ? 'Các bản tóm tắt của bạn'
                : 'Đăng nhập để xem story đã lưu'}
            </div>
          </div>
          {!isAuthenticated && (
            <div className="story-auth-actions">
              <Link to="/login" className="story-btn story-btn-primary">Đăng nhập</Link>
              <Link to="/register" className="story-btn">Đăng ký</Link>
            </div>
          )}
        </div>

        {!isAuthenticated ? (
          <div className="story-login-prompt">
            <p>Story chỉ hiển thị khi bạn đã đăng nhập.</p>
            <p className="muted">Dữ liệu tóm tắt khi chưa đăng nhập không được lưu lại sau khi rời trang.</p>
          </div>
        ) : (
        <div className="story-authenticated">
        <div className="story-filters">
          <div className="story-search">
            <div className="story-search-input">
              <Search size={18} />
              <input
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Tìm theo title (có thể kèm filter grade)..."
              />
            </div>
            <select value={searchGrade} onChange={(e) => setSearchGrade(e.target.value)}>
              <option value="">Grade (optional)</option>
              <option value="A">A</option>
              <option value="B">B</option>
              <option value="C">C</option>
              <option value="D">D</option>
              <option value="F">F</option>
            </select>
          </div>

          <div className="story-filter-row">
            <select value={status} onChange={(e) => setStatus(e.target.value)}>
              <option value="">Status</option>
              <option value="PENDING">PENDING</option>
              <option value="APPROVED">APPROVED</option>
              <option value="REJECTED">REJECTED</option>
            </select>

            <select value={grade} onChange={(e) => setGrade(e.target.value)}>
              <option value="">Grade</option>
              <option value="A">A</option>
              <option value="B">B</option>
              <option value="C">C</option>
              <option value="D">D</option>
              <option value="F">F</option>
            </select>

            <select value={method} onChange={(e) => setMethod(e.target.value)}>
              <option value="">Method</option>
              <option value="extractive">extractive</option>
              <option value="abstractive">abstractive</option>
              <option value="MAS">MAS</option>
              <option value="Normal">Normal</option>
            </select>

            <button type="button" className="story-btn" onClick={resetAll}>
              Reset
            </button>
          </div>

          <div className="story-hint">
            Chỉ hiển thị story thuộc tài khoản của bạn. Bộ lọc áp dụng trên danh sách cá nhân.
          </div>
        </div>

        <div className="story-grid">
          <div className="story-panel">
            <div className="panel-title">Top story của bạn (đọc nhiều nhất)</div>
            <div className="panel-body">
              {top10.length ? (
                <div className="story-list">
                  {top10.map((s) => (
                    <SummaryCard key={s.summaryId} s={s} onOpen={setSelected} />
                  ))}
                </div>
              ) : (
                <div className="muted">Chưa có dữ liệu.</div>
              )}
            </div>
          </div>

          <div className="story-panel">
            <div className="panel-title">Danh sách story của bạn</div>
            <div className="panel-body">
              {error ? <div className="story-error">{error}</div> : null}
              {loading ? <div className="muted">Đang tải...</div> : null}
              {!loading && !items.length ? <div className="muted">Không có kết quả.</div> : null}
              <div className="story-list">
                {items.map((s) => (
                  <SummaryCard key={s.summaryId} s={s} onOpen={setSelected} />
                ))}
              </div>
            </div>
          </div>
        </div>

        {selected ? (
          <div className="story-modal-backdrop" role="dialog" aria-modal="true" onClick={() => setSelected(null)}>
            <div className="story-modal" onClick={(e) => e.stopPropagation()}>
              <div className="story-modal-head">
                <div className="story-modal-title">{selected.title || '(No title)'}</div>
                <button type="button" className="story-btn" onClick={() => setSelected(null)}>
                  Đóng
                </button>
              </div>
              <div className="story-modal-meta">
                <span className="tag">{selected.grade || 'N/A'}</span>
                <span className="tag">{selected.method || 'N/A'}</span>
                <span className={`tag tag-${(selected.status || 'UNKNOWN').toLowerCase()}`}>{selected.status || 'UNKNOWN'}</span>
                <span className="muted">Reads: {Number(selected.readCount ?? 0)}</span>
                <span className="muted">By: {selected.createdByUserId || 'N/A'}</span>
                {selected.createdAt ? <span className="muted">{formatDate(selected.createdAt)}</span> : null}
              </div>

              <div className="story-modal-section">
                <div className="section-title">Văn bản gốc</div>
                <div className="section-content">{selected.content || '(trống)'}</div>
              </div>

              <div className="story-modal-section">
                <div className="section-title">Bản tóm tắt</div>
                {(() => {
                  const mapping = parseSummaryImageMapping(selected?.summaryImageUrl);
                  if (mapping.length) {
                    return (
                      <>
                        {mapping.map((item, idx) => (
                          <div key={idx} className="story-summary-part">
                            {item.imageUrl ? (
                              <div className="story-modal-img">
                                <img src={resolveImageSrc(item.imageUrl)} alt={`Summary part image ${idx + 1}`} />
                              </div>
                            ) : null}
                            {item.text ? <div className="section-content">{item.text}</div> : null}
                          </div>
                        ))}
                      </>
                    );
                  }

                  // Fallback: show images (from imageUrl) then full summaryContent
                  const imageUrls = parseImageUrls(selected?.imageUrl);
                  return (
                    <>
                      {imageUrls.length ? (
                        <div className="story-modal-img">
                          {imageUrls.map((u, idx) => (
                            <img key={idx} src={resolveImageSrc(u)} alt={`Summary image ${idx + 1}`} />
                          ))}
                        </div>
                      ) : null}
                      <div className="section-content">{selected.summaryContent || '(trống)'}</div>
                    </>
                  );
                })()}
              </div>
            </div>
          </div>
        ) : null}
        </div>
        )}
      </div>
    </div>
  );
}

