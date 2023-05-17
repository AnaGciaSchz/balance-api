FROM sameersbn/mysql:5.7.26-0

ENV DB_USER=balance \
    DB_PASS=123456789 \
    DB_NAME=dbbalance

EXPOSE 3306