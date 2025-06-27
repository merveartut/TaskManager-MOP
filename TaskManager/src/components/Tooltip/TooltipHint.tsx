import { Tooltip } from "@mui/material";
import React from "react";

interface TooltipProps {
  children: any;
  text: string;
  placement?: any;
}

export const TooltipHint: React.FC<TooltipProps> = ({
  children,
  text = "",
  placement = "top-start",
}) => {
  return (
    <Tooltip
      title={text}
      placement={placement}
      slotProps={{
        tooltip: {
          sx: { fontSize: "14px" }, // use `sx` for styling
        },
      }}
    >
      {children}
    </Tooltip>
  );
};
