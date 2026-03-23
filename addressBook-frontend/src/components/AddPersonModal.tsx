import { useEffect, useState } from "react";
import { ChevronDown, User } from "lucide-react";
import {
  EMPTY_PERSON_FORM,
  INDIAN_STATES,
  type PersonFormData,
} from "@/lib/people";

interface AddPersonModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (person: PersonFormData) => void | Promise<void>;
  initialData: PersonFormData;
  title: string;
  submitLabel: string;
  isSubmitting?: boolean;
}

export default function AddPersonModal({
  open,
  onClose,
  onSubmit,
  initialData,
  title,
  submitLabel,
  isSubmitting = false,
}: AddPersonModalProps) {
  const [formData, setFormData] = useState<PersonFormData>(initialData);

  useEffect(() => {
    if (open) {
      setFormData(initialData);
    }
  }, [initialData, open]);

  if (!open) return null;

  const updateField = (field: keyof PersonFormData, value: string) => {
    setFormData((currentFormData) => ({
      ...currentFormData,
      [field]: value,
    }));
  };

  const handleSubmit = () => {
    const hasEmptyField = Object.values(formData).some((value) => value.trim() === "");

    if (hasEmptyField || isSubmitting) {
      return;
    }

    void onSubmit(formData);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-foreground/40">
      <div className="w-full max-w-lg rounded-lg bg-card shadow-xl">
        <div className="flex items-center gap-2 border-b border-border px-6 py-4">
          <div className="flex h-8 w-8 items-center justify-center rounded bg-primary/10">
            <User className="h-4 w-4 text-primary" />
          </div>
          <h2 className="text-lg font-semibold text-card-foreground">{title}</h2>
        </div>

        <div className="space-y-4 px-6 py-5">
          <div>
            <label className="mb-1 block text-sm font-medium text-card-foreground">
              Full Name <span className="text-destructive">*</span>
            </label>
            <input
              type="text"
              placeholder="Enter full name"
              value={formData.fullName}
              onChange={(event) => updateField("fullName", event.target.value)}
              className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-card-foreground">
              Address <span className="text-destructive">*</span>
            </label>
            <textarea
              placeholder="Enter address"
              value={formData.address}
              onChange={(event) => updateField("address", event.target.value)}
              rows={3}
              className="flex min-h-[80px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-card-foreground">
                City <span className="text-destructive">*</span>
              </label>
              <input
                type="text"
                placeholder="Enter city"
                value={formData.city}
                onChange={(event) => updateField("city", event.target.value)}
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-card-foreground">
                State <span className="text-destructive">*</span>
              </label>
              <div className="relative">
                <select
                  value={formData.state}
                  onChange={(event) => updateField("state", event.target.value)}
                  className="flex h-10 w-full appearance-none rounded-md border border-input bg-background px-3 py-2 pr-10 text-sm focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
                >
                  <option value="" disabled>
                    Select state
                  </option>
                  {INDIAN_STATES.map((state) => (
                    <option key={state} value={state}>
                      {state}
                    </option>
                  ))}
                </select>
                <ChevronDown className="pointer-events-none absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 opacity-50" />
              </div>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-card-foreground">
                Zip Code <span className="text-destructive">*</span>
              </label>
              <input
                type="text"
                placeholder="Enter zip code"
                value={formData.zipCode}
                onChange={(event) => updateField("zipCode", event.target.value)}
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium text-card-foreground">
                Phone Number <span className="text-destructive">*</span>
              </label>
              <input
                type="text"
                placeholder="Enter phone number"
                value={formData.phoneNumber}
                onChange={(event) => updateField("phoneNumber", event.target.value)}
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
              />
            </div>
          </div>
        </div>

        <div className="flex justify-end gap-3 border-t border-border px-6 py-4">
          <button
            type="button"
            onClick={onClose}
            disabled={isSubmitting}
            className="inline-flex h-10 items-center justify-center rounded-md border border-input bg-background px-4 py-2 text-sm font-medium transition-colors hover:bg-accent hover:text-accent-foreground disabled:cursor-not-allowed disabled:opacity-50"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={handleSubmit}
            disabled={isSubmitting}
            className="inline-flex h-10 items-center justify-center rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-colors hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {isSubmitting ? "Saving..." : submitLabel}
          </button>
        </div>
      </div>
    </div>
  );
}
