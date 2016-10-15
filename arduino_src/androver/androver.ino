char terminator = '\n';
char type[2] = { 0, 0 };

// PINS
int IN1 = 4;
int IN2 = 5;
int PIN_SPEED_MOTOR_A = 6;
int IN3 = 8;
int IN4 = 9;
int PIN_SPEED_MOTOR_B = 10;

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
  
  // motor control front back
  if(type[0] == '0' && type[1] == '2'){
    motorControlProcessor(content);
    return;
  }
  // motor control left right
  if(type[0] == '0' && type[1] == '3'){
    leftRightProcessor(content);
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

// Message example: 02-1135
// >> 02  => message type
// >> 1   => direction
  // DIRECTION_NEUTRAL = "0";
  // DIRECTION_CLOCKWISE = "1";
  // DIRECTION_COUNT_CLOCKWISE = "2";
// >> 135 => PWM value

char lastState = '0';
double factorSpeedA = 1.0;
double factorSpeedB = 1.0;

//03-1030
void leftRightProcessor(String message){

  factorSpeedA = 1.0;
  factorSpeedB = 1.0;

  if (message.charAt(0) == '0') {
    // neutral
    return;
  }

  String _speed = message.substring(1, 4);
  int __speed = _speed.toInt();
  
  if (message.charAt(0) == '1') {
    // right
    factorSpeedB = 2.0;
    return;
  }
  if (message.charAt(0) == '2') {
    // left
    factorSpeedA = 2.0;
    return;
  }

}

void motorControlProcessor(String message){
  
  if(message.charAt(0) != lastState){
      lastState = message.charAt(0);
      stopRover();
      delay(500);      
  }

  String speedMotor = message.substring(1, 4);
   
  int speedMA = speedMotor.toInt() * factorSpeedA;
  int speedMB = speedMotor.toInt() * factorSpeedB;
  
  if(speedMA > 80) speedMA = 80;
  if(speedMB > 80) speedMB = 80;
    
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


