FROM postgres:16-bookworm

RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    python3-pip \
    curl \
    jq \
    && rm -rf /var/lib/apt/lists/*

RUN pip3 install --no-cache-dir --break-system-packages \
    "patroni[etcd3,etcd]" \
    psycopg2-binary

ENV PATH="/usr/lib/postgresql/16/bin:$PATH"

RUN mkdir -p /var/lib/postgresql/data /var/run/postgresql /tmp \
    && chown -R postgres:postgres /var/lib/postgresql /var/run/postgresql /tmp \
    && chmod 700 /var/lib/postgresql/data /var/run/postgresql

USER postgres

ENTRYPOINT ["patroni"]
