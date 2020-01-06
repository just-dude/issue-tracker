#!/bin/bash
echo "Wait dependencies"
wait_for "$WAIT_FOR_DB_URL" -t 60 -d
echo "Dependencies initialized"

# Обработчик завершения скрипта
# Нужен для того, чтобы завершить процесс приложения отправив ему сигнал SIGTERM
# Это необходимо, т.к. докер отправляет сигнал SIGTERM только текущему процессу, но не его дочерним
exitFunc() {
  echo "exit start"
  # Получаем pid приложения из файла
  appPid="$(cat /opt/application/bin/application.pid)"
  # Завершаем выполнение приложения
  kill -s SIGTERM $appPid
  # Дожидаемся завершения приложения
  while [ -e /proc/$appPid ]; do sleep .5; done
  # Завершаем выполнение вывода логов
  kill -s SIGTERM $updateTailPid
  kill -s SIGTERM $tailPid
  echo "exit end"
}

# Подписываемся на событие завершения скрипта
trap 'exitFunc' EXIT

# Файлы с логами приложения, которые будем выводить в stdout
log_files="/opt/application/logs/app.log";

# Вывод логов обновления
tail -F "/opt/db-updater/logs/app.log" 2>/dev/null &
# pid процесса вывода логов обновления
updateTailPid=$!
# Обновление БД
/opt/db-updater/bin/startup.sh
# Запуск приложения
/opt/application/bin/startup.sh &
# Вывод логов приложения
tail -F $log_files 2>/dev/null &
# pid процесса вывода логов приложения
tailPid=$!
# Дожидаемся завершения процесса вывода логов приложения,
# чтобы обработчик exitFunc сработал, когда docker отпраит сигнал завершения текщему процессу
wait $tailPid 2>/dev/null