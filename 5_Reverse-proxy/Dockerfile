FROM nginx:stable-alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY /ssl/us4.crt /etc/ssl/us4.crt
COPY /ssl/us4.key /etc/ssl/us4.key
COPY /error/fileSize.html /usr/share/nginx/html/fileSize.html
RUN apk update && apk add bash
EXPOSE 80 443