import React, { useState } from "react";
import { Input } from "../Input/Input";
import { Select } from "../Select/Select";

interface Field {
  name: string;
  label: string;
  type: string;
  isSingleSelect?: boolean;
  defaultValue?: string;
  displayLabel?: boolean;
  visible?: boolean;
  options?: { value: string; label: string }[];
}

interface FormProps {
  fields: Field[];
  onSubmit: (formData: Record<string, string | string[]>) => void;
  initialValues?: Record<string, any>;
  loading?: boolean;
}

export const Form: React.FC<FormProps> = ({
  fields,
  onSubmit,
  initialValues = {},
  loading = false,
}) => {
  const [formData, setFormData] = useState<Record<string, any>>(
    Object.fromEntries(
      fields.map((field) => [
        field.name,
        initialValues[field.name] ??
          (field.type === "picker" ? (field.isSingleSelect ? null : []) : ""),
      ])
    )
  );

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSelectChange = (name: string, selectedValues: any[] | any) => {
    setFormData({
      ...formData,
      [name]: selectedValues,
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };
  return (
    <form
      onSubmit={handleSubmit}
      className="p-4 border rounded-md flex items-center flex-col"
    >
      {fields.map(
        (field, index) =>
          field.visible &&
          (field.type === "picker" && field.options ? (
            <Select
              key={index}
              label={field.label}
              options={field.options}
              customClass="mb-6"
              displayLabel={field.displayLabel || false}
              isSingleSelect={field.isSingleSelect || false}
              selectedValues={field.defaultValue || formData[field.name]}
              onChange={(selected) => handleSelectChange(field.name, selected)}
            />
          ) : (
            <div className="mb-10" key={index}>
              <Input
                label={field.label}
                type={field.type}
                name={field.name}
                value={formData[field.name]}
                onChange={handleChange}
              />
            </div>
          ))
      )}
      {loading ? (
        <div className="flex justify-center items-center h-40">
          <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-purple-600"></div>
        </div>
      ) : (
        <button
          type="submit"
          disabled={loading}
          className="mt-4 bg-indigo-700 hover:bg-indigo-500 text-white py-2 px-4 rounded"
        >
          {loading ? (
            <span className="inline-block animate-spin rounded-full h-5 w-5 border-t-2 border-white border-solid"></span>
          ) : (
            "Submit"
          )}
        </button>
      )}
    </form>
  );
};
