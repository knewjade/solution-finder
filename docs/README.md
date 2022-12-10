# Setup for Python3 (venv)

```
cd docs
python3 -m venv .env
. .env/bin/activate
python3 -m pip install -r requirements.txt
```

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