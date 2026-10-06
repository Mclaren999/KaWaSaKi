# Pedro Pathing 3 — настройка KaWaSaKi

Проект уже содержит Pedro Pathing 3:

- `com.pedropathing:revhub:3.0.1`
- `com.pedropathing:tuning:1.0.1`

## Hardware Configuration

| Устройство | Имя |
|---|---|
| Передний левый мотор | `leftFront` |
| Задний левый мотор | `leftRear` |
| Передний правый мотор | `rightFront` |
| Задний правый мотор | `rightRear` |
| goBILDA Pinpoint | `pinpoint` |

Pinpoint должен быть подключён в I2C-порт, отличный от порта 0. Forward odometry pod подключается в X, strafe pod — в Y.

## Запуск AutoTune

1. Соберите проект и установите Robot Controller.
2. Подключитесь к сети Robot Controller.
3. Откройте `http://192.168.43.1:10158`.
4. Запускайте процедуры по порядку:
   1. **Mecanum Tuner**
   2. **Pinpoint Tuner**
   3. **Foresight Tuner**
   4. **Tests**

После каждой из первых трёх процедур откройте вкладку **Java** и перенесите сгенерированный блок в соответствующую переменную файла:

`TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/Constants.java`

- Mecanum → `drivetrainConfig`
- Pinpoint → `localizerConfig`
- Foresight → `foresightConfig`

## Важно

Текущие Pinpoint offsets равны нулю, а значения Foresight являются только стартовым шаблоном. До завершения AutoTune не используйте их для соревновательной автономки.

При проверке локализации:

- движение вперёд должно увеличивать X;
- движение влево должно увеличивать Y;
- поворот против часовой стрелки должен увеличивать heading.

Официальная документация: https://pedropathing.com/docs/pathing/pedro3
