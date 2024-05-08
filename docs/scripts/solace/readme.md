# Solace Samples Python

## Environment Setup

1. [Install Python3](https://www.python.org/downloads/) (See installed version using `python3 -V`)
   1.1 Note: If you are installing python for the first time on your machine then you can just use `python` instead of `python3` for the commands
2. [Optional] Install `virtualenv `python3 -m pip install --user virtualenv`` 1.1 Note: on a Linux machine, depending on the distribution you might need to`apt-get install python3-venv`instead 1.2 Alternatively, you can use`pyenv` to manage multiple versions of python. Follow the instructions here for more information https://realpython.com/intro-to-pyenv/#virtual-environments-and-pyenv
3. Clone this repository
4. [Optional] Setup python virtual environment `python3 -m venv venv`
5. [Optional] Activate virtual environment:
   1.1 MacOS/Linux: `source venv/bin/activate`
   1.2 Windows: `source venv/Scripts/activate`
6. After activating the virtual environment, make sure you have the latest pip installed `pip install --upgrade pip`

## Install the Solace Python API and other dependencies

1. Install the API `pip install -r requirements.txt`

## Start publisher

* Activate python virtual evironment

```
source venv/bin/activate
```

* Export solace connection variables

```
export SOLACE_HOST=20.79.213.119
export SOLACE_VPN=lidl-staging
export SOLACE_USERNAME=lidl-staging
export SOLACE_PASSWORD=jdn41\!SDF5
export SOLACE_TOPIC=aims/import/article
export SOLACE_QUEUE=aims.import.queue.article.v5.1.dev
```

* Start publisher

```
python3 guaranteed_publisher.py
```

## Start subscriber

* Activate python virtual evironment

```
source venv/bin/activate
```

* Export solace connection variables

```
export SOLACE_HOST=20.113.12.89
export SOLACE_VPN=lidl-staging
export SOLACE_USERNAME=lidl-staging
export SOLACE_PASSWORD=jdn41\!SDF5
export SOLACE_TOPIC=aims/import/article
export SOLACE_QUEUE=aims.import.queue.article
```

* Start subscriber

```
python3 guaranteed_subscriber.py
```
