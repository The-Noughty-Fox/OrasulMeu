###################
# BUILD FOR LOCAL DEVELOPMENT
###################

FROM node:18-alpine As development
RUN npm install -g pnpm

WORKDIR /usr/app

COPY pnpm-lock.yaml ./

RUN pnpm fetch --prod

COPY . .
RUN pnpm install

###################
# BUILD FOR PRODUCTION DEVELOPMENT
###################

FROM node:18-alpine As build
RUN npm install -g pnpm

WORKDIR /usr/app
COPY --chown=node:node pnpm-lock.yaml ./

COPY --chown=node:node --from=development /usr/app/node_modules ./node_modules
COPY --chown=node:node . .

RUN pnpm build

ENV NODE_ENV production

RUN pnpm install --prod

USER node

###################
# PRODUCTION
###################

FROM node:18-alpine As production

COPY --chown=node:node --from=build /usr/app/node_modules ./node_modules
COPY --chown=node:node --from=build /usr/app/dist ./dist

CMD [ "node", "dist/main.js" ]