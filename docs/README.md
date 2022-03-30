# Build the documents

```
cd docs
make html
```

# Prepare for translations

```
cd docs/source
sphinx-build -b gettext . ./_locale/gettext
```