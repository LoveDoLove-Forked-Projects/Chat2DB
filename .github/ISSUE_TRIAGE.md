# Issue Triage

Chat2DB uses five separate fields for issue management. Do not encode the same
meaning in more than one field.

| Field | Purpose |
| --- | --- |
| Template | Collect the required submission format and evidence |
| Issue Type | Define the primary nature: Task, Bug, or Feature |
| Label | Classify area, database, platform, edition, and evidence needs |
| Priority | Define urgency in the Community Project |
| Project Status | Track lifecycle from intake to completion |

The machine-readable taxonomy is [`issue-taxonomy.json`](issue-taxonomy.json).

## Templates And Types

| Template | Issue Type | Use |
| --- | --- | --- |
| Bug report | Bug | Reproducible Community behavior that is incorrect |
| Database compatibility bug | Bug | Database-specific connection, metadata, SQL, or editor behavior |
| Feature request | Feature | A new capability or product improvement |
| Documentation task | Task | Documentation corrections, additions, examples, or translations |

Questions belong in GitHub Discussions. Sensitive security reports are outside
this public issue process and must be submitted through the [security policy](../SECURITY.md).

## Labels

Labels are multi-select classifications:

- `area/*`: the owning product or code area.
- `db/*`: the affected database, when applicable.
- `platform/*`: the affected operating or deployment platform.
- `edition/*`: Community, Local, Pro, or unknown.
- `needs/*`: evidence or product decisions still required.
- `contribution/*`: tasks explicitly ready for external contributors.

After triage, an active issue must have:

1. exactly one Issue Type;
2. exactly one `edition/*` label;
3. at least one `area/*` or `db/*` label;
4. one Priority value;
5. one Project Status.

Do not create `type/*`, `priority/*`, or workflow-status labels. Issue Type,
Priority, and Project Status already own those dimensions.

## Form Option Mapping

Form options use contributor-facing language only. During triage, apply labels as
follows:

| Form value | Label |
| --- | --- |
| Web | `platform/web` |
| Desktop | `area/jcef` plus the selected operating-system platform |
| Docker | `platform/docker` |
| Windows, macOS, Linux | `platform/windows`, `platform/macos`, `platform/linux` |
| Database selection | matching `db/*` label, or `db/other` |
| AI | `area/ai` |
| Connection | `area/connection` |
| Database tree and metadata | `area/database-tree` |
| SQL execution or DDL, Database plugin | `area/backend` |
| SQL editor | `area/sql-editor` |
| Data editor | `area/data-editor` |
| Import or export | `area/import-export` |
| Desktop packaging | `area/jcef` |
| Docker build or image | `area/docker` |
| Documentation | `area/docs` |
| Other | `needs/decision` |

`area/docker` owns Dockerfile, image-build, and Compose code. `platform/docker`
means the observed problem only occurs in a Docker runtime. `area/frontend`
owns frontend code; `platform/web` means a web-only runtime problem.

## Priority

Priority is a single-select field in the Community Project. Reporters do not
assign it.

| Priority | Criteria | Response target |
| --- | --- | --- |
| P0 Critical | Security, data loss, startup failure, or release blocker with no workaround | Same day |
| P1 High | Core workflow unavailable for many users with no reliable workaround | 2 business days |
| P2 Normal | Normal confirmed issue or feature with limited impact or a workaround | 7 calendar days |
| P3 Low | Edge case, minor experience issue, or low-priority improvement | Monthly backlog review |

New issues start without a Priority. A triage maintainer assigns it after
checking impact, affected scope, reproducibility, and workarounds. P0 is limited
to Bugs and release-blocking Tasks. Sensitive security details remain private.

## Triage Procedure

1. Confirm the issue is for Chat2DB Community or apply the correct `edition/*`
   label and reroute it.
2. Confirm the Issue Type set by the template.
3. Add the primary `area/*` label and any applicable `db/*` or `platform/*`
   labels.
4. Add `needs/info`, `needs/reproduction`, or `needs/decision` when evidence or
   a product decision is missing.
5. Assign Priority, owner, target release, and Project Status.
6. Close duplicates or completed work with a concrete link and GitHub state
   reason.

The label sync script only creates or updates labels from the taxonomy. It
never deletes legacy labels.

## Legacy Label Migration

| Legacy label | New field |
| --- | --- |
| `bug` | Issue Type = Bug |
| `enhancement` | Issue Type = Feature |
| `AI-bug` | Type = Bug plus `area/ai` |
| Database `*-bug` labels | Type = Bug plus matching `db/*` |
| `connection`, `data editor`, `import/export`, `sql editor`, `ui`, `documentation` | matching `area/*` |
| `planned`, `Planning but not high priority` | Project Status and Priority |
| `wait for response`, `need testing`, `ambiguous`, `can't reproduce` | matching `needs/*` |
| `wait for review` | Project Status |
| `question` | GitHub Discussions Q&A |
| `Ch2DBPro` | `edition/pro` and the Pro support route |

Do not delete legacy labels until every attached issue has been migrated. During
migration, mark old labels as deprecated so maintainers do not apply them to new
issues.

## Rollout Order

1. Review the taxonomy and issue forms.
2. Run `script/github/sync-issue-labels.sh` without `--apply`.
3. Run the script with `--apply` to create the referenced labels.
4. Verify the labels, then merge and push the issue forms.
5. Create or validate the Project Priority field with
   `script/github/configure-issue-priority.sh`.

Issue forms silently skip labels that do not exist, so label creation must
happen before the forms become active. Until the Project exists, do not replace
Priority with priority labels.
