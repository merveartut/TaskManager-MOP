import { IconButton, InputAdornment, TextField } from "@mui/material";
import { Eye, EyeOff } from "lucide-react";
import React, { useState } from "react";

interface InputProps {
  label: string;
  type: string;
  name: string;
  value: string;
  variant?: "filled" | "outlined" | "standard";
  customClass?: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export const Input: React.FC<InputProps> = ({
  label,
  type,
  name,
  value,
  customClass,
  variant = "outlined",
  onChange,
}) => {
  const [showPassword, setShowPassword] = useState(false);
  const isPasswordType = type === "password";

  const handleClickShowPassword = () => setShowPassword(!showPassword);
  return (
    <div>
      <TextField
        id="outlined-basic"
        label={label}
        type={isPasswordType && !showPassword ? "password" : "text"}
        variant={variant}
        className={customClass}
        value={value}
        name={name}
        onChange={onChange}
        sx={{
          width: "300px",
          "& .MuiInputBase-root": {
            backgroundColor: "white",
          },
        }}
        style={{ width: "300px", backgroundColor: "white" }}
        InputProps={{
          endAdornment: isPasswordType && (
            <InputAdornment position="end">
              <IconButton onClick={handleClickShowPassword} edge="end">
                {showPassword ? <Eye /> : <EyeOff />}
              </IconButton>
            </InputAdornment>
          ),
        }}
      />
    </div>
  );
};
