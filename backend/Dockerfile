###################
# BUILD FOR LOCAL DEVELOPMENT
###################

FROM node:18-alpine As development
RUN npm install -g pnpm

WORKDIR /usr/app

COPY pnpm-lock.yaml ./
COPY package.json ./

RUN pnpm install

###################
# BUILD FOR PRODUCTION DEVELOPMENT
###################

FROM node:18-alpine As build
RUN npm install -g pnpm

WORKDIR /usr/app
COPY --chown=node:node --from=development /usr/app/pnpm-lock.yaml ./
COPY --chown=node:node --from=development /usr/app/node_modules ./node_modules
COPY --chown=node:node . .

RUN pnpm build

RUN pnpm install --prod

ENV NODE_ENV production

USER node

###################
# PRODUCTION
###################

FROM node:18-alpine As production

COPY --chown=node:node --from=build /usr/app/node_modules ./node_modules
COPY --chown=node:node --from=build /usr/app/dist ./dist

EXPOSE 8080
CMD [ "node", "dist/main.js" ]
