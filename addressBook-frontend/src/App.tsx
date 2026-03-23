import { useEffect, useState } from "react";
import { BookUser, Plus } from "lucide-react";
import AddPersonModal from "@/components/AddPersonModal";
import PersonTable from "@/components/PersonTable";
import { EMPTY_PERSON_FORM, type Person, type PersonFormData } from "@/lib/people";
import {
  createSimpleContact,
  deleteSimpleContactById,
  getAllSimpleContacts,
  updateSimpleContactById,
} from "@/lib/api";

export default function App() {
  const [persons, setPersons] = useState<Person[]>([]);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [editingPerson, setEditingPerson] = useState<Person | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const loadContacts = async () => {
    try {
      setIsLoading(true);
      setErrorMessage("");
      const contacts = await getAllSimpleContacts();
      setPersons(contacts);
    } catch (error) {
      setErrorMessage(
        error instanceof Error ? error.message : "Failed to load contacts.",
      );
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    void loadContacts();
  }, []);

  const handleAddPerson = async (formData: PersonFormData) => {
    try {
      setIsSaving(true);
      setErrorMessage("");
      const createdContact = await createSimpleContact(formData);
      setPersons((currentPersons) => [...currentPersons, createdContact]);
      setIsAddModalOpen(false);
    } catch (error) {
      setErrorMessage(
        error instanceof Error ? error.message : "Failed to create contact.",
      );
    } finally {
      setIsSaving(false);
    }
  };

  const handleUpdatePerson = async (formData: PersonFormData) => {
    if (!editingPerson) {
      return;
    }

    try {
      setIsSaving(true);
      setErrorMessage("");
      const updatedContact = await updateSimpleContactById(
        editingPerson.id,
        formData,
      );

      setPersons((currentPersons) =>
        currentPersons.map((person) =>
          person.id === editingPerson.id ? updatedContact : person,
        ),
      );
      setEditingPerson(null);
    } catch (error) {
      setErrorMessage(
        error instanceof Error ? error.message : "Failed to update contact.",
      );
    } finally {
      setIsSaving(false);
    }
  };

  const handleDeletePerson = async (personId: string) => {
    try {
      setErrorMessage("");
      await deleteSimpleContactById(personId);
      setPersons((currentPersons) =>
        currentPersons.filter((person) => person.id !== personId),
      );
    } catch (error) {
      setErrorMessage(
        error instanceof Error ? error.message : "Failed to delete contact.",
      );
    }
  };

  return (
    <div className="min-h-screen bg-background p-6">
      <div className="mx-auto max-w-7xl">
        <div className="mb-6 flex items-center gap-2">
          <BookUser className="h-6 w-6 text-primary" />
          <h1 className="text-xl font-bold text-foreground">ADDRESS BOOK</h1>
        </div>

        <div className="rounded-lg bg-card p-6 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold text-card-foreground">
              Person Details
            </h2>

            <button
              type="button"
              onClick={() => setIsAddModalOpen(true)}
              className="inline-flex h-10 items-center justify-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-colors hover:bg-primary/90"
            >
              <Plus className="h-4 w-4" />
              Add Person
            </button>
          </div>

          {errorMessage ? (
            <div className="mb-4 rounded-md border border-destructive/20 bg-destructive/5 px-4 py-3 text-sm text-destructive">
              {errorMessage}
            </div>
          ) : null}

          {isLoading ? (
            <div className="rounded-lg border border-border bg-card px-4 py-8 text-center text-sm text-muted-foreground">
              Loading contacts...
            </div>
          ) : (
            <PersonTable
              persons={persons}
              onEdit={setEditingPerson}
              onDelete={handleDeletePerson}
            />
          )}
        </div>
      </div>

      <AddPersonModal
        open={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSubmit={handleAddPerson}
        initialData={EMPTY_PERSON_FORM}
        title="Add Person"
        submitLabel="Add"
        isSubmitting={isSaving}
      />

      <AddPersonModal
        open={Boolean(editingPerson)}
        onClose={() => setEditingPerson(null)}
        onSubmit={handleUpdatePerson}
        initialData={editingPerson ?? EMPTY_PERSON_FORM}
        title="Edit Person"
        submitLabel="Update"
        isSubmitting={isSaving}
      />
    </div>
  );
}
