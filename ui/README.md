# Battleships Game

## Table of Contents

1. [Introduction](#introduction)
2. [Configuration](#configuration)
3. [Local Development](#local-development)

## Introduction

Battleships Game is a web application to play the Battleships game.

It uses

* Vite for build tool
* React for frontend
* TypeScript for programming language
* Material-UI for UI components
* Axios for HTTP requests

## Configuration

Configuration can be modified via [.env](.env) and [.env.local](.env.local) files or following environment variables.

| Variable Name | Data Type | Description              |
|---------------|-----------|--------------------------|
| VITE_API_URL  | String    | Base URL for game server |

## Local Development

You'll need [node](https://nodejs.org) installed. Then you can use following npm tasks

* `npm install` to install dependencies
* `npm run dev` to run the application in development mode
* `npm run build` to build the application for production
