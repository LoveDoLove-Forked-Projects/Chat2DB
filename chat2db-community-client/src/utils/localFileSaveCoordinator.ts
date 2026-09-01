export interface LocalFileSaveRequest {
  filePath: string;
  fileContent: string;
  charset?: string;
  bom?: boolean;
}

export interface LocalFileSaveResult {
  filePath: string;
  fileContent: string;
}

type LocalFileSaveMutation = (request: LocalFileSaveRequest) => Promise<unknown>;

interface SaveWaiter {
  resolve: (result: LocalFileSaveResult) => void;
  reject: (error: unknown) => void;
}

interface PendingSave {
  request: LocalFileSaveRequest;
  waiters: SaveWaiter[];
}

interface FileSaveState {
  pending?: PendingSave;
  draining: boolean;
}

export function normalizeLocalFileSaveKey(filePath: string) {
  const rawPath = filePath.replace(/\\/g, '/');
  const leadingSlashes = rawPath.startsWith('//') ? '//' : '';
  return `${leadingSlashes}${rawPath.slice(leadingSlashes.length).replace(/\/+/g, '/')}`;
}

export class LocalFileSaveCoordinator {
  private readonly states = new Map<string, FileSaveState>();

  save(request: LocalFileSaveRequest, mutation: LocalFileSaveMutation) {
    const key = normalizeLocalFileSaveKey(request.filePath);
    let state = this.states.get(key);
    if (!state) {
      state = { draining: false };
      this.states.set(key, state);
    }

    return new Promise<LocalFileSaveResult>((resolve, reject) => {
      if (state!.pending) {
        state!.pending.request = request;
        state!.pending.waiters.push({ resolve, reject });
      } else {
        state!.pending = {
          request,
          waiters: [{ resolve, reject }],
        };
      }
      if (!state!.draining) {
        state!.draining = true;
        void this.drain(key, state!, mutation);
      }
    });
  }

  private async drain(key: string, state: FileSaveState, mutation: LocalFileSaveMutation) {
    while (state.pending) {
      const pending = state.pending;
      state.pending = undefined;
      try {
        await mutation(pending.request);
        if (state.pending) {
          state.pending.waiters.push(...pending.waiters);
          continue;
        }
        const result = {
          filePath: pending.request.filePath,
          fileContent: pending.request.fileContent,
        };
        pending.waiters.forEach(({ resolve }) => resolve(result));
      } catch (error) {
        if (state.pending) {
          state.pending.waiters.push(...pending.waiters);
          continue;
        }
        pending.waiters.forEach(({ reject }) => reject(error));
      }
    }

    state.draining = false;
    if (!state.pending) {
      this.states.delete(key);
    }
  }
}

export const localFileSaveCoordinator = new LocalFileSaveCoordinator();
