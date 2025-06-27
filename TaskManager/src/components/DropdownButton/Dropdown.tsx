import { Menu } from "@mui/material";
import { ChevronDown } from "lucide-react";
import React from "react";

interface DropdownProps {
  label?: string;
  customButtonClass?: string;
  options: string[];
  selectedOption?: string;
  onSelect?: (value: string) => void;
  disabled?: boolean;
}

export const Dropdown: React.FC<DropdownProps> = ({
  label = "Options",
  options,
  selectedOption,
  customButtonClass,
  disabled = false,
  onSelect,
}) => {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  const handleSelect = (option: string) => {
    onSelect?.(option);
    handleClose();
  };
  return (
    <div>
      <button
        id="demo-customized-button"
        aria-controls={open ? "demo-customized-menu" : undefined}
        className={customButtonClass}
        aria-haspopup="true"
        aria-expanded={open ? "true" : undefined}
        onClick={handleClick}
        disabled={disabled}
      >
        <div className="flex flex-row">
          {selectedOption || label} <ChevronDown />
        </div>
      </button>
      <Menu
        id="demo-positioned-menu"
        aria-labelledby="demo-positioned-button"
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "right",
        }}
        transformOrigin={{
          vertical: "top",
          horizontal: "right",
        }}
      >
        {options.map((option) => (
          <div
            key={option}
            onClick={() => handleSelect(option)}
            className={`p-2 cursor-pointer hover:bg-indigo-100 ${
              option === selectedOption ? "bg-sky-100" : ""
            }`}
          >
            {option}
          </div>
        ))}
      </Menu>
    </div>
  );
};
