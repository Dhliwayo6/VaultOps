import { useEffect, useRef } from 'react';

/**
 * Custom hook to trap keyboard focus within a container.
 * Also handles Esc key triggers and restores focus to the previously active element on unmount.
 * 
 * @param {boolean} isOpen - Whether the focus trap is active
 * @param {function} onClose - Callback function to invoke on Escape keypress
 * @returns {React.RefObject} Ref to be attached to the container element
 */
export function useFocusTrap(isOpen, onClose) {
  const containerRef = useRef(null);
  const previousActiveElementRef = useRef(null);

  useEffect(() => {
    if (isOpen) {
      // Save currently active element to restore focus when closed
      previousActiveElementRef.current = document.activeElement;

      const container = containerRef.current;
      if (!container) return;

      // Selectable focusable elements
      const focusableSelector = 'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])';
      
      // Delay slightly to allow the DOM to render and transition
      const timer = setTimeout(() => {
        const focusableElements = container.querySelectorAll(focusableSelector);
        if (focusableElements.length > 0) {
          focusableElements[0].focus();
        } else {
          container.focus();
        }
      }, 50);

      const handleKeyDown = (e) => {
        if (e.key === 'Escape') {
          if (onClose) {
            e.preventDefault();
            onClose();
          }
          return;
        }

        if (e.key !== 'Tab') return;

        const currentFocusables = Array.from(container.querySelectorAll(focusableSelector));
        if (currentFocusables.length === 0) {
          e.preventDefault();
          return;
        }

        const firstElement = currentFocusables[0];
        const lastElement = currentFocusables[currentFocusables.length - 1];

        if (e.shiftKey) {
          // Shift + Tab: if on first element, wrap to last
          if (document.activeElement === firstElement) {
            lastElement.focus();
            e.preventDefault();
          }
        } else {
          // Tab: if on last element, wrap to first
          if (document.activeElement === lastElement) {
            firstElement.focus();
            e.preventDefault();
          }
        }
      };

      document.addEventListener('keydown', handleKeyDown);

      return () => {
        clearTimeout(timer);
        document.removeEventListener('keydown', handleKeyDown);
        // Restore focus to triggering element
        if (previousActiveElementRef.current && typeof previousActiveElementRef.current.focus === 'function') {
          setTimeout(() => {
            previousActiveElementRef.current.focus();
          }, 50);
        }
      };
    }
  }, [isOpen, onClose]);

  return containerRef;
}
