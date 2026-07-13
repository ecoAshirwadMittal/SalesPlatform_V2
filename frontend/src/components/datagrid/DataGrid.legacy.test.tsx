// @vitest-environment jsdom
import { describe, it, expect, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import DataGrid, { type ColumnDef, type FetcherArgs } from "./DataGrid";

// RBL-P4 / RBL-P5: the reserve-bids page opts into variant="legacy" for the
// Mendix DataGrid-2 chrome. These tests pin both the legacy path AND that the
// default ("app") path — used by the out-of-scope inventory grid — is
// unchanged, so the opt-in prop cannot regress inventory.

interface Row {
  id: number;
  name: string;
}

const columns: ColumnDef<Row>[] = [
  { key: "name", label: "Name", accessor: (r) => r.name },
];

const rows: Row[] = [{ id: 1, name: "Alpha" }, { id: 2, name: "Beta" }];

function fetcherFor(total: number) {
  return vi.fn(async (_args: FetcherArgs) => ({ rows, total }));
}

describe("DataGrid legacy variant", () => {
  it('renders the plain "N to M of T" pager (no "Showing", no thousands separator)', async () => {
    render(
      <DataGrid<Row>
        variant="legacy"
        columns={columns}
        fetcher={fetcherFor(1234)}
        rowKey={(r) => r.id}
        rowActions={(r) => <button type="button">audit {r.id}</button>}
      />,
    );
    await waitFor(() => expect(screen.getByText("1 to 20 of 1234")).toBeInTheDocument());
    expect(screen.queryByText(/Showing/)).not.toBeInTheDocument();
    // 1234 must render un-grouped — the legacy footer has no comma.
    expect(screen.queryByText(/1,234/)).not.toBeInTheDocument();
  });

  it("puts the column-visibility eye inside the action-column header (not a toolbar button)", async () => {
    render(
      <DataGrid<Row>
        variant="legacy"
        columns={columns}
        fetcher={fetcherFor(2)}
        rowKey={(r) => r.id}
        rowActions={(r) => <button type="button">audit {r.id}</button>}
      />,
    );
    await waitFor(() => expect(screen.getByText("Alpha")).toBeInTheDocument());
    // No standalone "Columns" toolbar button in legacy chrome.
    expect(screen.queryByText("Columns")).not.toBeInTheDocument();
    // The single column-visibility control lives inside a <th>.
    const eye = screen.getByRole("button", { name: "Toggle column visibility" });
    expect(eye.closest("th")).not.toBeNull();
  });
});

describe("DataGrid default (app) variant is unchanged", () => {
  it('renders the "Showing N to M of T,TTT" pager and a toolbar Columns button', async () => {
    render(
      <DataGrid<Row>
        columns={columns}
        fetcher={fetcherFor(1234)}
        rowKey={(r) => r.id}
        rowActions={(r) => <button type="button">edit {r.id}</button>}
        rowActionsLabel="Actions"
      />,
    );
    await waitFor(() =>
      expect(screen.getByText("Showing 1 to 20 of 1,234")).toBeInTheDocument(),
    );
    // Column selector stays a toolbar button (not in a header cell).
    const eye = screen.getByRole("button", { name: "Toggle column visibility" });
    expect(eye.closest("th")).toBeNull();
    expect(screen.getByText("Columns")).toBeInTheDocument();
    // Action header shows the label text, not the eye.
    expect(screen.getByRole("columnheader", { name: "Actions" })).toBeInTheDocument();
  });
});
