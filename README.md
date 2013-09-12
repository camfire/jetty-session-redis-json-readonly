jetty-redis-json-sessions
===============================

# Description

This project provides a way to read from a redis session store from scala under jetty. This includes custom jetty
session management that can be plugged in. I use the term 'session-management' loosely as the intent is to never
create, delete, or write to the sessions from scala. I simply want to load session that are previously created externally.

I have a web front end project written in rails that handles user authentication and session creation. I want to use those
sessions to require a user has a session before allowing any access to my project.

Specifically, I am using nathantsoi's redis-store-json project on the rails side to actually create my sessions.

# Assumptions

Since we are only reading, we need to prevent access from requests that do not have a session already associated. A filter
can be used.

# Credit

Ovea's jetty-session-redis project (https://github.com/Ovea/jetty-session-redis)

nathantsoi's redis-store-json project (https://github.com/nathantsoi/redis-store-json)

