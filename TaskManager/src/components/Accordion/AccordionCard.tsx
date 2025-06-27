import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import AccordionDetails from "@mui/material/AccordionDetails";
import { ChevronDown } from "lucide-react";

interface AccordionProps {
  header: string;
  content: any;
  customClass?: string;
  defaultExpanded?: boolean;
}
export const AccordionCard: React.FC<AccordionProps> = ({
  header,
  content,
  defaultExpanded,
  customClass,
}) => {
  return (
    <Accordion className={customClass} defaultExpanded={defaultExpanded}>
      <AccordionSummary
        expandIcon={<ChevronDown />}
        aria-controls="panel1-content"
        id="panel1-header"
      >
        <span>{header}</span>
      </AccordionSummary>
      <AccordionDetails>{content}</AccordionDetails>
    </Accordion>
  );
};
