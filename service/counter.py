import logging
from logging.handlers import RotatingFileHandler
from flask import Flask

app = Flask(__name__)

v=0

@app.route("/add")
def add1():
    global v
    v += 1
    app.logger.error('add1 %d' % v)
    return str(v)

@app.route("/minus")
def minus1():
    global v
    v -= 1
    app.logger.error('minus1 %d' % v)
    return str(v)

if __name__ == "__main__":
    handler = RotatingFileHandler('foo.log', maxBytes=10000, backupCount=1)
    handler.setLevel(logging.INFO)
    app.logger.addHandler(handler)
    app.run()
