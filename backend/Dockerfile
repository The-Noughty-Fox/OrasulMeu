FROM node:18.16.0-alpine3.17

# Install ssh client, git, and bash
RUN apk add --no-cache openssh-client git bash

# Download public key for github.com
RUN mkdir -p -m 0600 ~/.ssh && ssh-keyscan github.com >> ~/.ssh/known_hosts

WORKDIR /usr/app

COPY package.json pnpm-lock.yaml* /usr/app/
RUN npm install -g pnpm
RUN pnpm install

# Installing ts-node-dev globally using pnpm
RUN pnpm add -g ts-node-dev

# Remove any existing .tsbuildinfo files
RUN rm -f *.tsbuildinfo

# Copy the rest of the application code
COPY . /usr/app

EXPOSE 8080

# Use pnpm to run the start script
CMD ["pnpm", "run", "start:prod"]
