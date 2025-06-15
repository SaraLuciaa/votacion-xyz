#!/bin/bash

echo "Matando procesos anteriores de IceGrid..."
pkill -f icegridnode
pkill -f icegridregistry
pkill -f icegridadmin
sleep 1

echo "Eliminando datos previos de IceGrid..."
rm -rf db/registry db/nodo-central
mkdir -p db/registry db/nodo-central

echo "Iniciando IceGrid Registry..."
icegridregistry --Ice.Config=grid.cfg &
sleep 2

echo "Iniciando IceGrid Node..."
icegridnode --Ice.Config=nodeCentral.cfg &
sleep 2



echo "Todo listo. Puedes ejecutar tus clientes ahora."
