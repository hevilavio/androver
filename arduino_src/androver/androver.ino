char terminator = '\n';
char type[2] = { 0, 0 };

// PINS
int IN1 = 4;
int IN2 = 5;
int PIN_SPEED_MOTOR_A = 6;
int IN3 = 8;
int IN4 = 9;
int PIN_SPEED_MOTOR_B = 10;

/*
ARDUINO -> COMPONENT

5V  -> 5V L2
GND -> GND L2
GND -> GND HT (3RD PIN)
RX  -> TX HT (4TH PIN)
TX  -> RESISTOR RX HT (5TH PIN)
P4  -> IN1
P5  -> IN2
P6  -> ENA

P8  -> IN3
P9  -> IN4
P10 -> ENB

MOTOR LEFT:
YELLOW -> OUT1
GREEN  -> OUT2

MOTOR RIGHT:
YELLOW -> OUT4
GREEN  -> OUT3

L298N:
12V CONFIG WITH JUMPING

*/

void setup()
{
  Serial.begin(9600);
  Serial.flush();

  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(PIN_SPEED_MOTOR_A, OUTPUT);

  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);
  pinMode(PIN_SPEED_MOTOR_B, OUTPUT);

  delay(1000);
}
 
void loop()
{
  while (Serial.available() == 0)
   /* just wait */ ;
  
  String message = receiveMessage();
  
  if (message != "") {
    type[0] = message.charAt(0);
    type[1] = message.charAt(1);
    
    String content = message.substring(2);
    routeToMessageProcessor(type, content);
  }
}

String receiveMessage(){
  String message = "";  
  char character = ' ';
  while(character != terminator ) {
      character = Serial.read();
      if(character != -1) message.concat(character);
  }
  return message;
}

void routeToMessageProcessor(char type[], String content){
  
  // ping
  if(type[0] == '0' && type[1] == '1'){
    pingMessageProcessor(content);
    return;
  }
  
  // motionControlProcessor
  if(type[0] == '0' && type[1] == '4'){
    motionControlProcessor(content);
    return;
  }
  
  Serial.print("could not find message, content=");
  Serial.print(content);
  Serial.print(terminator);
}

// 01
void pingMessageProcessor(String message){
  Serial.print("01");
  Serial.print(message);
}


/*
example:
  0400021060

04 - motionControlProcessor
0  - forward / backward
005- forward / backward speed
2  - left/right
027- left/right speed 
*/

char lastState = '0';
void motionControlProcessor(String message){
  
  if(message.charAt(0) != lastState){
      lastState = message.charAt(0);
      stopRover();
      delay(500);      
  }

  String speedMotor = message.substring(1, 4);
  int speedMA = speedMotor.toInt();
  int speedMB = speedMotor.toInt();
  
  char _direction = message.charAt(4);
  String speedDirection = message.substring(5, 8);
  if (_direction == '0'){
    // nothing
  }
  if (_direction == '1'){
    // left
    speedMA += speedDirection.toInt();
    speedMB -= speedDirection.toInt();
  }
  
  if (_direction == '2'){
    // right
    speedMB += speedDirection.toInt();
    speedMA -= speedDirection.toInt();
  }
   
  
  if(speedMA > 120) speedMA = 120;
  if(speedMB > 120) speedMB = 120;

  if(speedMA < 0) speedMA = 0;
  if(speedMB < 0) speedMB = 0;


    
  analogWrite(PIN_SPEED_MOTOR_A, speedMA);
  analogWrite(PIN_SPEED_MOTOR_B, speedMB);
  
  if (message.charAt(0) == '0'){
    roverOnNeutral();
  } 
  else if (message.charAt(0) == '1'){
    // back
    roverOnCountClockWise();
  } 
  else if (message.charAt(0) == '2'){
    // forward
    roverOnClockWise();
  }
}


void stopRover(){
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, HIGH); 
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, HIGH); 
}
void roverOnNeutral() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, LOW);
}
void roverOnClockWise(){
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, LOW);
}
void roverOnCountClockWise(){
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);
}


