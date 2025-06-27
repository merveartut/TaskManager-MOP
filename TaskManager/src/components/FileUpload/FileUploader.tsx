import { FileUpload } from "primereact/fileupload";
import React from "react";
import toast from "react-hot-toast";

interface FileProps {
  taskId?: any;
  userId?: any;
  label?: string;
  disabled?: boolean;
  onUploadComplete?: () => void;
}

const API_BASE = import.meta.env.VITE_API_URL;
export const FileUploader: React.FC<FileProps> = ({
  taskId,
  userId,
  label = "Upload",
  disabled = false,
  onUploadComplete,
}) => {
  const handleUpload = async (event: any) => {
    const file = event.files[0];

    const formData = new FormData();
    formData.append("file", file);
    formData.append("taskId", taskId);
    formData.append("userId", userId);

    try {
      const response = await fetch(`${API_BASE}/api/attachments/upload`, {
        method: "POST",
        body: formData,
      });

      if (response.ok) {
        toast.success("File uploaded successfully!");
        onUploadComplete?.();
      } else {
        alert("Failed to upload");
      }
    } catch (err) {
      console.error(err);
      alert("Upload error");
    }
  };
  return (
    <FileUpload
      name="file"
      customUpload
      uploadHandler={handleUpload}
      mode="basic"
      auto
      disabled={disabled}
      className={`!p-0 text-white rounded cursor-pointer`}
      chooseLabel={label}
    />
  );
};
