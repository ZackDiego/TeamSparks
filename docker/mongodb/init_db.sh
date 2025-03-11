#!/bin/bash
# Wait for MongoDB to start up
echo "Waiting for MongoDB to start..."
sleep 10

# Import the BSON data using mongorestore
echo "Restoring data from BSON file..."

mongorestore --uri="mongodb://root:mongodb_pass@localhost:27017" --authenticationDatabase=admin --db=teamSparks /docker-entrypoint-initdb.d/channel_message_histories.bson
