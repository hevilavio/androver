char terminator = '\n';
char type[2] = { 0, 0 };

// PINS
int IN1 = 4;
int IN2 = 5;
int PIN_SPEED_MOTOR_A = 6;

void setup()
{
  Serial.begin(9600);
  Serial.flush();

  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(PIN_SPEED_MOTOR_A, OUTPUT);

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
  
  // motor control
  if(type[0] == '0' && type[1] == '2'){
    motorControlProcessor(content);
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
void motorControlProcessor(String message){
  
  if(message.charAt(0) != lastState){
      lastState = message.charAt(0);
      stopRover();
      delay(500);      
  }

  String speedMotor = message.substring(1, 4);
  analogWrite(PIN_SPEED_MOTOR_A, speedMotor.toInt());
  
  if (message.charAt(0) == '0'){
    roverOnNeutral();
  } 
  else if (message.charAt(0) == '1'){
    roverOnClocWise();
  } 
  else if (message.charAt(0) == '2'){
    roverOnCountClocWise();
  }
}


void stopRover(){
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, HIGH); 
}
void roverOnNeutral() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
}
void roverOnClocWise(){
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
}
void roverOnCountClocWise(){
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
}


