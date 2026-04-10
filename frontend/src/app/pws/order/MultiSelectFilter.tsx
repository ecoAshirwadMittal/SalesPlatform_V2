'use client';

import { useState, useRef, useEffect } from 'react';
import styles from './pwsOrder.module.css';

interface MultiSelectFilterProps {
  options: string[];
  selected: string[];
  onChange: (selected: string[]) => void;
  placeholder?: string;
}

export default function MultiSelectFilter({ options, selected, onChange, placeholder = 'All' }: MultiSelectFilterProps) {
  const [open, setOpen] = useState(false);
  const wrapperRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    }
    if (open) {
      document.addEventListener('mousedown', handleClickOutside);
      return () => document.removeEventListener('mousedown', handleClickOutside);
    }
  }, [open]);

  const toggleOption = (value: string) => {
    if (selected.includes(value)) {
      onChange(selected.filter(v => v !== value));
    } else {
      onChange([...selected, value]);
    }
  };

  const label = selected.length === 0
    ? placeholder
    : selected.length === 1
      ? selected[0]
      : `${selected.length} selected`;

  return (
    <div className={styles.multiSelectWrapper} ref={wrapperRef}>
      <button
        type="button"
        className={styles.multiSelectTrigger}
        onClick={() => setOpen(prev => !prev)}
        title={selected.join(', ') || placeholder}
      >
        {label}
      </button>
      {open && (
        <div className={styles.multiSelectDropdown}>
          {selected.length > 0 && (
            <button
              type="button"
              className={styles.multiSelectClear}
              onClick={() => { onChange([]); setOpen(false); }}
            >
              Clear filter
            </button>
          )}
          {options.map(opt => (
            <label key={opt} className={styles.multiSelectOption}>
              <input
                type="checkbox"
                className={styles.multiSelectCheckbox}
                checked={selected.includes(opt)}
                onChange={() => toggleOption(opt)}
              />
              {opt}
            </label>
          ))}
          {options.length === 0 && (
            <div className={styles.multiSelectOption} style={{ color: '#999', fontStyle: 'italic' }}>
              No options
            </div>
          )}
        </div>
      )}
    </div>
  );
}
