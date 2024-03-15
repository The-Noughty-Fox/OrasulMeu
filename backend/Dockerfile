FROM node:18.16.0-alpine3.17

# Install ssh client and git
RUN apk add --no-cache openssh-client git

# Download public key for github.com
RUN mkdir -p -m 0600 ~/.ssh && ssh-keyscan github.com >> ~/.ssh/known_hosts
RUN apk update && apk add bash

WORKDIR /usr/app
ARG CACHEBUSTER=unknown
COPY package*.json ./
RUN rm -f *.tsbuildinfo
RUN npm install -g ts-node-dev
RUN echo "$CACHEBUSTER" && npm install
COPY . .

EXPOSE 8080
CMD ["npm", "run", "start:prod"]
