# Filestore

Filestore dynamically populates the game cache with assets at runtime.
This process only needs to be performed once, or whenever the cache assets change.

## Usage

### 1. Back Up the Cache

Run the backup script before making any changes to the cache:

```bash
bash Scripts/backup_cache.sh
```

### 2. Compile the Project

Compile the project using Maven:

```bash
mvn compile
```

### 3. Run Filestore

Execute the following command to populate the cache with assets:

```bash
mvn compile exec:java
```

### 4. Restore the Cache (Optional)

To restore the cache from a previously created backup, run:

```bash
bash Scripts/replace_cache.sh
```

On Windows, you can also run the **[Shell Script] Replace Cache** configuration in IntelliJ IDEA, which executes the script using Git Bash.

This restores the cache from the backup instead of regenerating it.
