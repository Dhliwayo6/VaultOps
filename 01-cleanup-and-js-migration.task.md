# Task 01: Structural Cleanup and TypeScript to JavaScript Migration

## Context

This is the first execution phase after `ANALYSIS.md`. The stack decision for this project is React + JavaScript + Vite on the frontend and Spring Boot + MySQL on the backend. The current frontend under `fontend/` is TypeScript (`.tsx`, `tsconfig*.json`). Before any feature work continues, the project needs a clean, correctly named, plain JavaScript foundation.

## Goals

1. Fix all file and folder naming problems identified in the analysis.
2. Convert the entire frontend from TypeScript to plain JavaScript.
3. Leave the project in a state where `npm run dev` and `npm run build` both succeed with zero TypeScript remnants.

## Tasks

### Folder and file cleanup
- [ ] Move all contents of `fontend/` into a correctly named `frontend/` folder at the repository root, then delete the old `fontend/` and the pre-existing empty `frontend/` folder so there is exactly one frontend folder.
- [ ] Delete the empty `Client/` folder at the repository root.
- [ ] Delete `structure.txt` at the repository root and `backend/vaultops/structre.txt`.
- [ ] Add a `.gitignore` entry (or confirm one exists) for `target/`, `node_modules/`, `dist/`, `*.class`, and jacoco output, so these never get committed again.
- [ ] Move `Command.java` and `Query.java` out of the root `com.vaultops` package into a new `com.vaultops.common` package (or fold their shared logic into a single interface if a later phase decides the CQRS split is not worth keeping. do not decide that here, just relocate for now).

### TypeScript to JavaScript conversion
- [ ] Convert every `.tsx` file to `.jsx` and every `.ts` file to `.js` under `frontend/src`.
- [ ] Remove all type annotations, interfaces, type aliases, and generic type parameters. Convert TypeScript-only patterns (enums, `as` casts) to plain JavaScript equivalents (plain objects for enums, no casts).
- [ ] Remove `tsconfig.json`, `tsconfig.app.json`, `tsconfig.node.json` from `frontend/`.
- [ ] Remove `typescript`, `@types/*`, and any TypeScript-only ESLint plugins from `frontend/package.json`.
- [ ] Update `vite.config.js` (renamed from `.ts` if needed) to a plain JavaScript Vite config for a React project.
- [ ] Update `eslint.config.js` to a plain JavaScript/React ESLint config (drop `@typescript-eslint` rules, keep `eslint-plugin-react-hooks` and `eslint-plugin-react-refresh`).
- [ ] Confirm `index.html` and any import paths reference `.jsx`/`.js` files correctly after the rename.

### Sanity checks
- [ ] Run the dev server and confirm the app boots with no console errors related to missing types or broken imports.
- [ ] Run a production build and confirm it completes with no TypeScript-related errors (there should be none left to produce, this is a final confirmation the migration is complete).
- [ ] Grep the entire `frontend/` folder for the strings `.ts"`, `.tsx"`, `: string`, `: number`, `interface `, and `type ` to catch anything missed by the conversion.

## Acceptance Criteria

- There is exactly one frontend folder, named `frontend`, containing only `.js` and `.jsx` source files.
- No TypeScript config, dependency, or type annotation remains anywhere in the project.
- `Client/`, the duplicate `frontend/`, `structure.txt`, and `structre.txt` no longer exist.
- `Command.java` and `Query.java` no longer sit at the root of `com.vaultops`.
- The app runs and builds cleanly.

## Constraints

- Do not change any component's visual output or behavior in this task. This is a mechanical conversion and cleanup pass only. Behavioral fixes (mocked auth, broken sidebar state, enum mismatches) are handled in later tasks.
- Do not touch backend Java logic beyond the two file relocations above.
- Commit in small, reviewable chunks: one commit for folder/file cleanup, one commit for the TS to JS conversion.
