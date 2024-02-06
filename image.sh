echo Building Dockerfile described image
docker build -t order .
echo Launching docker compose services for upstanding order:latest image
docker-compose up