import { ChevronDown, X } from "lucide-react";
import React, { useEffect, useRef, useState } from "react";

interface SelectProps {
  label: string;
  options: any[];
  isSingleSelect?: boolean;
  selectedValues: any[] | any;
  customClass?: string;
  displayLabel?: boolean;
  displayClearButtonForSingleSelect?: boolean;
  clearSelected?: () => void;
  onChange: (selected: string[] | string) => void;
}

export const Select: React.FC<SelectProps> = ({
  label,
  options,
  customClass,
  displayLabel = false,
  isSingleSelect = false,
  selectedValues,
  displayClearButtonForSingleSelect = false,
  clearSelected,
  onChange,
}) => {
  const [search, setSearch] = useState("");
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Filter options based on search input
  const filteredOptions = options.filter((option) => {
    if (option.name) {
      return (
        option?.name?.toLowerCase().includes(search.toLowerCase()) &&
        (isSingleSelect
          ? selectedValues !== option
          : !selectedValues.includes(option))
      );
    } else if (option.title) {
      return (
        option?.title?.toLowerCase().includes(search.toLowerCase()) &&
        (isSingleSelect
          ? selectedValues !== option
          : !selectedValues.includes(option))
      );
    } else {
      return (
        option.toLowerCase().includes(search.toLocaleLowerCase()) &&
        (isSingleSelect
          ? selectedValues !== option
          : !selectedValues.includes(option))
      );
    }
  });

  const handleSelect = (value: string) => {
    if (!isSingleSelect && selectedValues.includes(value)) {
      onChange(selectedValues.filter((v: any) => v !== value));
    } else {
      if (isSingleSelect) {
        onChange(value);
        setIsOpen(false);
      } else {
        onChange([...selectedValues, value]);
      }
    }
  };

  const removeSelected = (e: any, value: any) => {
    e.preventDefault();
    onChange(selectedValues.filter((v: any) => v !== value));
  };
  const getOptionLabel = (opt: any) => {
    if (typeof opt === "string") return opt;
    if (opt.name) return opt.name;
    if (opt.title) return opt.title;
    return JSON.stringify(opt);
  };
  return (
    <div className={`relative ${customClass} w-full`} ref={dropdownRef}>
      {displayLabel && (
        <label className="flex items-start text-sm font-medium text-gray-700">
          {label}
        </label>
      )}

      {/* Input Field (Searchable) */}
      <div
        className={`border border-gray-300 rounded-md shadow-sm p-2 flex flex-wrap flex-row items-center justify-between cursor-pointer w-full h-[55px]`}
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className="flex flex-wrap gap-2 items-center overflow-y-auto max-h-[100px]">
          {isSingleSelect ? (
            <div className="flex items-center justify-between gap-2">
              {selectedValues ? (
                <span className="truncate">
                  {getOptionLabel(selectedValues)}
                </span>
              ) : (
                <span className="text-gray-400">{label}</span>
              )}
              {displayClearButtonForSingleSelect && selectedValues && (
                <button
                  onClick={clearSelected}
                  className="bg-indigo-500 text-white px-2 py-1 rounded-md text-sm ml-2 flex items-center"
                >
                  <X size={18} />
                </button>
              )}
            </div>
          ) : selectedValues?.length > 0 ? (
            <>
              {selectedValues.slice(0, 3).map((value: any, index: number) => (
                <span
                  key={index}
                  className="bg-indigo-500 text-white px-2 py-1 rounded-md text-sm"
                  onClick={(e) => removeSelected(e, value)}
                >
                  {getOptionLabel(value)} ✖
                </span>
              ))}
              {selectedValues.length > 3 && (
                <span className="text-sm text-gray-500 ml-1">
                  +{selectedValues.length - 3} more
                </span>
              )}
            </>
          ) : (
            <span className="text-gray-400">{label}</span>
          )}
        </div>

        <ChevronDown
          className={`ml-auto text-gray-500 transition-transform duration-200 ${
            isOpen ? "rotate-180" : ""
          }`}
        />
      </div>

      {/* Dropdown Options */}
      {isOpen && (
        <div className="absolute w-full bg-white border border-gray-300 rounded-md shadow-md mt-1 max-h-48 overflow-auto z-10">
          <input
            type="text"
            placeholder="Search..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full p-2 border-b border-gray-200 outline-none"
          />
          {filteredOptions.length > 0 ? (
            filteredOptions.map((option, index) => (
              <div
                key={index}
                onClick={() => handleSelect(option)}
                className={`p-2 cursor-pointer hover:bg-blue-100 flex items-start pl-4 `}
              >
                {getOptionLabel(option)}
              </div>
            ))
          ) : (
            <div className="p-2 text-gray-500">No data found</div>
          )}
        </div>
      )}
    </div>
  );
};
