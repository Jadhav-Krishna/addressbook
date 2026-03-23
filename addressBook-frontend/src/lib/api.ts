import type { Person, PersonFormData } from "@/lib/people";

const API_BASE_URL = "http://localhost:8080";
const SIMPLE_CONTACTS_PATH = "/api/simple/contacts";
const ADVANCED_CONTACTS_PATH = "/api/contacts";

type ApiContact = {
  id?: string | number;
  contactId?: string | number;
  fullName?: string;
  name?: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string | number;
  zip?: string | number;
  postalCode?: string | number;
  phoneNumber?: string | number;
  phone?: string | number;
  mobile?: string | number;
};

function buildUrl(path: string) {
  return `${API_BASE_URL}${path}`;
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(buildUrl(path), {
    headers: {
      "Content-Type": "application/json",
      ...init?.headers,
    },
    ...init,
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || `Request failed with status ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

function normalizeContact(contact: ApiContact): Person {
  return {
    id: String(contact.id ?? contact.contactId ?? ""),
    fullName: contact.fullName ?? contact.name ?? "",
    address: contact.address ?? "",
    city: contact.city ?? "",
    state: contact.state ?? "",
    zipCode: String(contact.zipCode ?? contact.zip ?? contact.postalCode ?? ""),
    phoneNumber: String(
      contact.phoneNumber ?? contact.phone ?? contact.mobile ?? "",
    ),
  };
}

function toApiPayload(contact: PersonFormData) {
  return {
    fullName: contact.fullName,
    name: contact.fullName,
    address: contact.address,
    city: contact.city,
    state: contact.state,
    zipCode: contact.zipCode,
    phoneNumber: contact.phoneNumber,
  };
}

export async function getAllSimpleContacts(): Promise<Person[]> {
  const contacts = await request<ApiContact[]>(SIMPLE_CONTACTS_PATH);
  return contacts.map(normalizeContact);
}

export async function getSimpleContactById(id: string): Promise<Person> {
  const contact = await request<ApiContact>(`${SIMPLE_CONTACTS_PATH}/${id}`);
  return normalizeContact(contact);
}

export async function createSimpleContact(contact: PersonFormData): Promise<Person> {
  const createdContact = await request<ApiContact>(SIMPLE_CONTACTS_PATH, {
    method: "POST",
    body: JSON.stringify(toApiPayload(contact)),
  });
  return normalizeContact(createdContact);
}

export async function updateSimpleContactById(
  id: string,
  contact: PersonFormData,
): Promise<Person> {
  const updatedContact = await request<ApiContact>(`${SIMPLE_CONTACTS_PATH}/${id}`, {
    method: "PUT",
    body: JSON.stringify(toApiPayload(contact)),
  });
  return normalizeContact(updatedContact);
}

export async function deleteSimpleContactById(id: string): Promise<void> {
  await request<void>(`${SIMPLE_CONTACTS_PATH}/${id}`, {
    method: "DELETE",
  });
}

export async function getAllContacts(): Promise<Person[]> {
  const contacts = await request<ApiContact[]>(ADVANCED_CONTACTS_PATH);
  return contacts.map(normalizeContact);
}

export async function getContactById(id: string): Promise<Person> {
  const contact = await request<ApiContact>(`${ADVANCED_CONTACTS_PATH}/${id}`);
  return normalizeContact(contact);
}

export async function createContact(contact: PersonFormData): Promise<Person> {
  const createdContact = await request<ApiContact>(ADVANCED_CONTACTS_PATH, {
    method: "POST",
    body: JSON.stringify(toApiPayload(contact)),
  });
  return normalizeContact(createdContact);
}

export async function updateContactById(
  id: string,
  contact: PersonFormData,
): Promise<Person> {
  const updatedContact = await request<ApiContact>(`${ADVANCED_CONTACTS_PATH}/${id}`, {
    method: "PUT",
    body: JSON.stringify(toApiPayload(contact)),
  });
  return normalizeContact(updatedContact);
}

export async function deleteContactById(id: string): Promise<void> {
  await request<void>(`${ADVANCED_CONTACTS_PATH}/${id}`, {
    method: "DELETE",
  });
}

export async function updateContactByName(
  name: string,
  contact: PersonFormData,
): Promise<Person> {
  const updatedContact = await request<ApiContact>(
    `${ADVANCED_CONTACTS_PATH}/name/${encodeURIComponent(name)}`,
    {
      method: "PUT",
      body: JSON.stringify(toApiPayload(contact)),
    },
  );
  return normalizeContact(updatedContact);
}

export async function deleteContactByName(name: string): Promise<void> {
  await request<void>(`${ADVANCED_CONTACTS_PATH}/name/${encodeURIComponent(name)}`, {
    method: "DELETE",
  });
}
