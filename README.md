# Descuentados

Descuentados es una aplicación que permite mantener un registro de códigos de descuentos actualizados en los vídeos de YouTube.

También permite calcular las ganancias de las comisiones en los últimos meses mostrando comparaciones entre los meses.

## Uso
Deberás descargar la versión correspondiente a tu sistema operativo e instalarla. Podrás encontrarlas en la sección de [releases](https://github.com/miguelangel_lv/descuentados/releases).

### Youtube

Para poder actualizar los códigos de descuento en los vídeos de YouTube, deberás configurar la aplicación con tu cuenta de YouTube. Para ello
necesitas un certificado OAuth 2.0. Puedes seguir los siguientes pasos:

#### Login con Google
1. Accede a [Google Cloud API](https://console.cloud.google.com/apis/credentials)
2. Pulsa en crear nuevos credenciales y selecciona de tipo OAuth Client ID
3. Selecciona «Aplicación de Escritorio» e introduce un nombre.
4. Descarga el JSON generado y guárdalo en tu equipo
5. En la aplicación, ve a la sección de configuración y selecciona el archivo JSON descargado.

#### Plantillas para los códigos
En la sección de configuración encontrarás las plantillas necesarias para actualizar los códigos de descuento.
La aplicación buscará los _inicio_ y _fin_ configurados en la descripción de cada vídeo y sustituirá todo el contenido entre ellos por los códigos de descuento actualizados.

En caso de no encontrar el _inicio_ o el _fin_, mostrará error.


### Comisiones
En la sección de comisiones podrás cargar un .csv descargado de [Portal de Aliexpress](https://portals.aliexpress.com/affiportals/web/order_tracking.htm)