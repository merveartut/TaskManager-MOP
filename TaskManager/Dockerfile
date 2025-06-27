# 1. Aşama: React uygulamasını build et
FROM node:18 as build

WORKDIR /app

# Önce package.json ve package-lock.json (varsa) kopyala
COPY package*.json ./

# Bağımlılıkları yükle
RUN npm install

# Tüm dosyaları kopyala
COPY . .

# React uygulamasını üretim için derle
RUN npm run build

# 2. Aşama: Oluşan build dosyalarını nginx ile servis et
FROM nginx:alpine

# React build klasörünü nginx’in serve ettiği dizine kopyala
COPY --from=build /app/dist /usr/share/nginx/html

# 80 portunu aç
EXPOSE 80

# Nginx’i foreground’da çalıştır
CMD ["nginx", "-g", "daemon off;"]
