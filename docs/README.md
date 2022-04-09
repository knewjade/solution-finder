# Build the documents

```
cd docs
make -e SPHINXOPTS="-D language='ja'" html
make -e SPHINXOPTS="-D language='en'" html
```

# Prepare for translations

```
cd docs/source
sphinx-build -b gettext . ./locale/gettext && sphinx-intl update -p locale/gettext -l en
```