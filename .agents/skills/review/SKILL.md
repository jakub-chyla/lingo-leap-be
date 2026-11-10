---
name: review-skill
description: Use when the user asks for code review, reviewing Git changes, or reviewing and fixing code. Analyze changed files or the entire project depending on the user's request, fix confirmed issues directly in the code, add tests, and never commit or push.
---

# Code Review and Fix Skill

## 1. Main Objective

Perform a thorough code review and fix identified problems directly in the source code.

**This is NOT a report-only review.**

You MUST:
- Analyze the relevant code.
- Identify real bugs, vulnerabilities, and design problems.
- Fix every confirmed issue.
- Add or update tests where appropriate.
- Run relevant tests and build.
- Verify the final changes.

Do not stop after identifying problems. Continue with implementation and verification.

Do not introduce artificial changes just to demonstrate activity.

## 2. Determine Review Scope

First, inspect the repository:

- Run `git status`.
- Inspect staged changes.
- Inspect unstaged changes.
- Identify untracked files.
- Inspect the project structure.
- Identify the technologies and conventions used by the project.

Choose the scope based on the user's request:

### Git Changes Review

If the user asks to review changes, focus on:
- Staged changes.
- Unstaged changes.
- Related classes and dependencies needed to understand the changes.

Do not review only staged changes if unstaged changes are also relevant.

### Full Project Audit

If the user asks for a full project audit, review the entire application regardless of Git status.

Inspect all relevant:
- Controllers and REST endpoints.
- Services and business logic.
- Repositories and database queries.
- Entities and DTOs.
- Security configuration.
- Authentication and authorization.
- Exception handling.
- Configuration and environment variables.
- Tests.
- Frontend components and services, if applicable.

Do not assume that reviewing Git changes is equivalent to auditing the entire project.

## 3. Security

Check for:

### Authentication and Authorization
- IDOR – unauthorized access to another user's resources by manipulating IDs.
- Broken Access Control.
- Missing authentication and authorization checks.
- Missing resource-level ownership validation.
- Fail-Closed – deny access by default when security checks fail.
- Incorrect role and permission validation.
- Mass Assignment vulnerabilities.

### Input and Output Security
- Missing input validation.
- SQL Injection.
- XSS.
- CSRF.
- Insecure deserialization.
- Command Injection.
- Path Traversal.
- Unsafe file uploads.
- Sensitive data exposure.

### Configuration and Secrets
- Hardcoded passwords, API keys, tokens, and secrets.
- Insecure CORS configuration.
- Incorrect security headers.
- Exposed internal endpoints.
- Insecure authentication token handling.
- Missing rate limiting where appropriate.

Never expose sensitive information in logs, API responses, or exceptions.

## 4. Clean Code

Check for:

- Appropriate naming of classes, methods, variables, and constants.
- SRP – Single Responsibility Principle.
- Small and focused classes and methods.
- DRY – avoid unnecessary duplication.
- KISS – keep implementations simple.
- YAGNI – avoid unnecessary functionality.
- Proper encapsulation and access modifiers.
- Avoidance of magic numbers and magic strings.
- Readable and self-documenting code.
- No unnecessary comments.
- No unused variables, methods, imports, or dependencies.
- Consistent formatting and project conventions.

Do not refactor code solely because you prefer a different coding style.

## 5. Performance and Memory

Check for:

- Memory leaks.
- Incorrect resource lifecycle management.
- N+1 database queries.
- Missing or inefficient database indexes.
- Unnecessary database queries.
- Excessive data loading.
- Unnecessary object creation and processing.
- Incorrect thread management.
- Incorrect connection pool configuration.
- Blocking operations in asynchronous or reactive code.
- Missing pagination for large datasets.
- Unnecessary HTTP requests.
- Inefficient frontend rendering.

## 6. Testing

Check for:

- Unit tests.
- Integration tests.
- Business logic coverage.
- Edge cases.
- Error handling tests.
- Security and authorization tests.
- Regression tests.
- Correct mocking.
- Test isolation.
- Meaningful assertions.
- Flaky tests.

When fixing a bug:
- Add a regression test whenever practical.
- Verify that the test fails before the fix when feasible.
- Verify that the test passes after the fix.

Do not modify tests just to make broken code appear correct.

## 7. Architecture

Check for:

- SOLID principles.
- DRY, KISS, and YAGNI.
- Separation of concerns.
- Separation of business logic from infrastructure.
- Low coupling and high cohesion.
- Circular dependencies.
- Correct dependency injection.
- Appropriate use of design patterns.
- Correct responsibility of architectural layers.
- Consistency with the existing project architecture.

Avoid unnecessary abstractions and architectural changes.

## 8. Error Handling

Check for:

- Correct exception handling.
- No empty catch blocks.
- No swallowed exceptions.
- Appropriate exception types.
- Centralized exception handling where appropriate.
- Correct HTTP error responses.
- Logging without sensitive data.
- Proper timeout configuration.
- Safe retry policies.
- Correct transaction management.
- Proper rollback behavior.
- No unsafe retries of non-idempotent operations.

## 9. API and Data

Check for:

- Correct HTTP methods and status codes.
- Consistent API contracts.
- DTO validation.
- No direct exposure of database entities.
- Correct pagination, sorting, and filtering.
- Resource-level authorization.
- Correct null and optional value handling.
- Database constraints.
- Data consistency.
- Transactional integrity.
- Backward compatibility.
- Correct handling of concurrent updates.

## 10. Java and Spring Boot

Additionally check:

- Correct Spring dependency injection.
- Appropriate service and repository responsibilities.
- Correct transaction boundaries (`@Transactional`).
- No unnecessary transactions.
- Correct transaction propagation.
- Proper use of `Optional`.
- No unsafe `.get()` calls on potentially empty Optional values.
- Correct exception handling.
- Bean lifecycle and scope.
- Correct JPA relationships and fetch strategies.
- No accidental eager loading.
- Correct cascade operations.
- No unintended entity serialization.
- Proper DTO mapping.
- Correct validation using Jakarta Validation.
- Proper Spring Security configuration.
- No authorization bypasses.
- Correct configuration through application properties and environment variables.
- Proper external service communication.
- Correct timeout and retry configuration.
- Thread safety where applicable.
- No shared mutable state without proper synchronization.

## 11. Angular

Additionally check:

### Components and Architecture
- Single Responsibility Principle.
- Proper separation between components and services.
- Appropriate component size and complexity.
- Correct dependency injection.
- Proper use of standalone components.
- Appropriate lazy loading.

### Signals and RxJS
- Correct use of Signals and RxJS.
- Avoid unnecessary subscriptions.
- Prevent memory leaks.
- Proper subscription cleanup.
- Correct use of `computed`, `effect`, and `untracked`.
- Avoid unnecessary conversions between Signals and Observables.
- Correct asynchronous data handling.

### Performance
- Correct use of `ChangeDetectionStrategy.OnPush`.
- Avoid unnecessary component rendering.
- Avoid unnecessary HTTP requests.
- Track collections correctly.
- Avoid expensive operations in templates.
- Proper use of lazy loading and code splitting.

### Forms and Validation
- Correct use of Reactive Forms.
- Proper synchronous and asynchronous validation.
- Correct handling of form errors.
- No duplicated validation logic.

### Security and Accessibility
- Correct handling of authentication and authorization.
- No sensitive information in localStorage unless justified.
- Avoid unsafe DOM manipulation.
- Correct handling of untrusted HTML.
- WCAG accessibility.
- Correct keyboard navigation.
- Proper ARIA attributes where necessary.

### Resource Management
- Cleanup of subscriptions, effects, event listeners, and resources.
- Correct use of lifecycle hooks.
- No memory leaks caused by unmanaged resources.

## 12. Maintainability

Check for:

- Unused code and dependencies.
- Clear project structure.
- Consistent naming.
- Formatting and linting.
- Secure and maintained dependencies.
- Appropriate documentation.
- Avoidance of unnecessary complexity.
- Consistency with existing coding standards.

## 13. Resilience and External Services

Check for:

- Appropriate connection and request timeouts.
- Retry with exponential backoff where appropriate.
- Retry only when safe.
- Circuit Breaker where justified.
- Bulkhead isolation where justified.
- Rate limiting.
- Graceful degradation.
- Proper handling of external service failures.
- Idempotency of critical operations.
- Correct handling of duplicate messages.
- Proper Kafka and RabbitMQ message processing where applicable.
- Dead Letter Queue handling where appropriate.
- Correct acknowledgment and retry behavior.
- No message loss during processing failures.

Do not introduce resilience patterns without a real use case.

## 14. Review Priorities

Classify findings:

- CRITICAL – severe security vulnerability, data loss, or major system compromise.
- HIGH – significant functional bug, security issue, or major failure.
- MEDIUM – meaningful bug, performance issue, or architectural problem.
- LOW – minor issue with limited impact.

Only report actionable and justified findings.

Do not report speculative issues without sufficient evidence.

## 15. Mandatory Fix Procedure

After identifying problems:

1. Prioritize findings by severity.
2. Understand the root cause.
3. Fix confirmed issues directly in the source code.
4. Add or update tests.
5. Preserve existing functionality.
6. Avoid unrelated refactoring.
7. Run relevant tests.
8. Run the build.
9. Run linting or static analysis if available.
10. Inspect the final Git diff.

If an issue cannot be safely fixed:
- Explain why.
- Describe the potential impact.
- Do not pretend it has been fixed.

If no issues are found:
- Do not modify code unnecessarily.
- Explicitly report that no actionable issues were identified.

## 16. Git Safety Rules

- Never execute `git commit`.
- Never execute `git push`.
- Never create a pull request.
- Never discard user changes.
- Never execute destructive Git commands such as `git reset --hard` or `git clean -fd`.
- Never overwrite unrelated modifications.
- Preserve existing staged and unstaged changes.
- Do not stage additional files unless explicitly requested.
- Do not modify Git history.

## 17. Final Report

After completing the work, provide:

### Summary
Brief description of the review scope.

### Findings
List identified issues with severity and explanation.

### Fixes
Describe what was changed and why.

### Modified Files
List all modified files.

### Tests
List executed tests and their results.

### Build
Report build status.

### Remaining Issues
List unresolved issues and explain why they remain unresolved.

If no issues were found, explicitly state:

"No actionable issues found. No code changes were necessary."

**IMPORTANT: The task is complete only after the applicable review, fixes, and verification steps have been performed. Never commit or push changes.**