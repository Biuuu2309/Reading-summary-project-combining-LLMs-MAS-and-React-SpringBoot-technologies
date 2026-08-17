const MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
const ACCEPTED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/bmp'];

export function validateImageFile(file) {
  if (!file) return 'Không có file ảnh';
  if (!ACCEPTED_TYPES.includes(file.type)) {
    return 'Chỉ hỗ trợ ảnh JPG, PNG, GIF, WEBP, BMP';
  }
  if (file.size > MAX_IMAGE_SIZE) {
    return 'Ảnh quá lớn (tối đa 5MB)';
  }
  return null;
}

export function fileToBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result;
      if (typeof result !== 'string') {
        reject(new Error('Không đọc được ảnh'));
        return;
      }
      const base64 = result.includes(',') ? result.split(',')[1] : result;
      resolve(base64);
    };
    reader.onerror = () => reject(new Error('Không đọc được ảnh'));
    reader.readAsDataURL(file);
  });
}

export function fileToPreviewUrl(file) {
  return URL.createObjectURL(file);
}
