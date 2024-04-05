###################
# BUILD FOR LOCAL DEVELOPMENT
###################

FROM node:18-alpine As development
RUN npm install -g pnpm

WORKDIR /usr/app

COPY pnpm-lock.yaml ./
COPY package.json ./
COPY uploads ./

RUN pnpm install

###################
# BUILD FOR PRODUCTION DEVELOPMENT
###################

FROM node:18-alpine As build
RUN npm install -g pnpm

WORKDIR /usr/app
COPY --chown=node:node pnpm-lock.yaml ./
COPY --chown=node:node uploads ./

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
COPY --chown=node:node --from=build /usr/app/uploads ./dist/uploads

CMD [ "node", "dist/main.js" ]
EXPOSE 8080