# Task 06: Authentication and Security

## Context

`ANALYSIS.md` found that authentication does not exist on the backend at all, and that the frontend `SignIn`, `SignUp`, and `OtpActivation` pages simulate success with `setTimeout` rather than calling a real API. This task builds real authentication end to end.

## Tasks

### Backend authentication
- [ ] Add Spring Security and JWT dependencies to `pom.xml`.
- [ ] Create a `User` entity (email, hashed password, name, role, account status) and a corresponding Flyway migration and repository.
- [ ] Hash passwords with BCrypt on registration, never store or log plaintext passwords.
- [ ] Build a registration endpoint that creates a user in a pending state and triggers an OTP (a random, time-limited numeric code) sent by email through whatever transactional email provider is already configured elsewhere in the broader project, or a simple SMTP/log-based stand-in if no provider is wired up yet for this project specifically.
- [ ] Build an OTP verification endpoint that activates the user account on a correct, unexpired code, and rejects expired or incorrect codes with a clear error.
- [ ] Build a login endpoint that verifies credentials and returns a signed JWT (with a reasonable expiry, for example a few hours) plus a refresh mechanism (a longer lived refresh token, or a simple re-login flow if a full refresh token system is out of scope for now, document the choice either way).
- [ ] Add a Spring Security filter chain that requires a valid JWT for all asset, maintenance, migration, stats, import, and export endpoints, while leaving registration, login, and OTP verification public.
- [ ] Add role-based authorization if the project needs more than one role (for example a plain user versus an admin who can delete assets or manage users). If only one role is needed for now, document that decision rather than over-building.
- [ ] Configure CORS explicitly to only allow the known frontend origin(s), not a wildcard, in every environment.
- [ ] Add tests for registration, OTP verification (success, wrong code, expired code), login (success and failure), and access to a protected endpoint with and without a valid token.

### Frontend integration
- [ ] Replace the `setTimeout` simulation in `SignUp.tsx`/`.jsx` with a real call to the registration endpoint, showing real success and error states.
- [ ] Replace the OTP activation simulation with a real call to the verification endpoint.
- [ ] Replace the `HandleLoginAsync` stub in `SignIn` with a real call to the login endpoint, storing the returned JWT securely (in memory plus a refresh strategy, or an httpOnly cookie if the backend is set up to issue one, avoid `localStorage` for the token if at all reasonably possible given the security tradeoffs).
- [ ] Add an authenticated API client (a thin wrapper around `fetch`) that attaches the JWT to every request to a protected endpoint and handles a 401 by redirecting to sign in.
- [ ] Add a route guard so `/portal` and its children are inaccessible without a valid, non-expired token, redirecting to sign in otherwise.

## Acceptance Criteria

- No page simulates success with a timer. Every auth flow makes a real network call and reflects real success or failure.
- Every non-auth backend endpoint requires a valid JWT.
- CORS is locked to known origins, not wildcarded.
- Passwords are never stored or transmitted in plaintext.
- Route guarding on the frontend actually prevents access to the portal without a valid session, verified manually or with a test.

## Constraints

- This task depends on Task 01 (frontend is plain JS by the time this work starts) and Task 04 (consolidated services, so protected endpoints are stable before wrapping them in security).
- Do not build a full OAuth/social login system unless specifically requested elsewhere. Email and password with OTP activation is the scope here.
