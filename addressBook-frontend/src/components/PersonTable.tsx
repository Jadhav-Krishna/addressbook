import { Person } from "@/lib/people";
import { Trash2, Pencil } from "lucide-react";

interface PersonTableProps {
  persons: Person[];
  onEdit: (person: Person) => void;
  onDelete: (id: string) => void;
}

export default function PersonTable({ persons, onEdit, onDelete }: PersonTableProps) {
  return (
    <div className="overflow-x-auto rounded-lg border border-border bg-card">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-border bg-muted text-left text-muted-foreground">
            <th className="px-4 py-3 font-medium">Fullname</th>
            <th className="px-4 py-3 font-medium">Address</th>
            <th className="px-4 py-3 font-medium">City</th>
            <th className="px-4 py-3 font-medium">State</th>
            <th className="px-4 py-3 font-medium">Zip Code</th>
            <th className="px-4 py-3 font-medium">Phone Number</th>
            <th className="px-4 py-3 font-medium"></th>
          </tr>
        </thead>
        <tbody>
          {persons.map((person) => (
            <tr
              key={person.id}
              className="border-b border-border last:border-b-0 hover:bg-muted/50 transition-colors"
            >
              <td className="px-4 py-3 text-card-foreground">{person.fullName}</td>
              <td className="px-4 py-3 text-card-foreground">{person.address}</td>
              <td className="px-4 py-3 text-card-foreground">{person.city}</td>
              <td className="px-4 py-3 text-card-foreground">{person.state}</td>
              <td className="px-4 py-3 text-card-foreground">{person.zipCode}</td>
              <td className="px-4 py-3 text-card-foreground">{person.phoneNumber}</td>
              <td className="px-4 py-3">
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => onDelete(person.id)}
                    className="rounded p-1 text-destructive hover:bg-destructive/10 transition-colors"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                  <button
                    onClick={() => onEdit(person)}
                    className="rounded p-1 text-primary hover:bg-primary/10 transition-colors"
                  >
                    <Pencil className="h-4 w-4" />
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
