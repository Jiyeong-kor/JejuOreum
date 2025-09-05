# Architecture

This project is being migrated to a **hybrid architecture** that combines traditional layered design (presentation, domain, data) with feature-based organization. Each feature now owns its own internal layers under `app/src/main/java/com/jeong/jjoreum/feature/<name>/`.

For example, the `list` and `join` features are organized as:

```
feature/
  list/
    data/
    domain/
    presentation/
  join/
    data/
    domain/
    presentation/
```

Shared components that are used across multiple features remain in their existing locations for now. Additional features will be migrated over time.
