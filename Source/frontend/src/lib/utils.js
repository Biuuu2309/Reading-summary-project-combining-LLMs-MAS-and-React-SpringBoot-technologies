import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";
// Utility function to merge class names with Tailwind
export function cn(...inputs) {
    return twMerge(clsx(inputs));
}
// Utility function to format a number with currency
export function formatCurrency(amount, currency = "USD", options) {
    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency,
        ...options,
    }).format(amount);
}
// Utility function to generate a unique ID
export function generateUniqueId(prefix = "id") {
    return `${prefix}-${Math.random().toString(36).substring(2, 9)}`;
}
// Utility function to truncate text
export function truncateText(text, maxLength) {
    if (text.length <= maxLength)
        return text;
    return text.substring(0, maxLength) + "...";
}
// Utility function to parse backend timestamp strings safely
export function parseTimestamp(value) {
    if (value == null || value === "")
        return null;
    if (value instanceof Date)
        return Number.isNaN(value.getTime()) ? null : value;
    const str = String(value).trim();
    if (!str)
        return null;
    const normalized = str.replace(/\sICT\s/, " GMT+07:00 ");
    const parsed = new Date(normalized);
    if (!Number.isNaN(parsed.getTime()))
        return parsed;
    const fallback = new Date(str);
    return Number.isNaN(fallback.getTime()) ? null : fallback;
}
// Utility function to format date
export function formatDate(date, options) {
    const parsed = parseTimestamp(date);
    if (!parsed)
        return "Invalid date";
    return new Intl.DateTimeFormat("en-US", {
        day: "numeric",
        month: "short",
        year: "numeric",
        ...options,
    }).format(parsed);
}
// Utility function to debounce function calls
export function debounce(func, wait) {
    let timeout = null;
    return function (...args) {
        const later = () => {
            timeout = null;
            func(...args);
        };
        if (timeout !== null) {
            clearTimeout(timeout);
        }
        timeout = setTimeout(later, wait);
    };
}
// Utility function to throttle function calls
export function throttle(func, limit) {
    let inThrottle = false;
    return function (...args) {
        if (!inThrottle) {
            func(...args);
            inThrottle = true;
            setTimeout(() => {
                inThrottle = false;
            }, limit);
        }
    };
}
