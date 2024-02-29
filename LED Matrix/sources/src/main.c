#include <stdio.h>
#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/pgmspace.h>
#include <avr/eeprom.h>
#include "serial_printf.h"
#include <i2cmaster.h>

#define HIGH                        1
#define LOW                         0
#define true                        1
#define false                       0

//Pinos para as entradas na matriz
#define CLK                         (1<<PINB5)     
#define LOAD                        (1<<PINB2)      
#define DIN                         (1<<PINB3)    

//Número de blocos de 8x8 existentes na matriz  
#define BLOCOS                      4 

//Registos da matriz retirados da datasheet
#define NO_OP                       0x00
#define DIGIT0                      0x01
#define DIGIT1                      0x02
#define DIGIT2                      0x03
#define DIGIT3                      0x04
#define DIGIT4                      0x05
#define DIGIT5                      0x06
#define DIGIT6                      0x07
#define DIGIT7                      0x08
#define DEC_MODE                    0x09
#define INTENSITY                   0x0A
#define SCAN_LIMIT                  0x0B
#define SHUTDOWN                    0x0C
#define DISP_TEST                   0x0F

//Registos do RTC DS3231 retirados da datasheet
#define DS3231_ReadMode_U8          0xD1
#define DS3231_WriteMode_U8         0xD0
#define DS3231_REG_Seconds          0x00
#define DS3231_REG_Minutes          0x01
#define DS3231_REG_CONTROL          0x0E

/*Os pinos do RTC DS3231 são o SCL e SDA que ligam 
aos pinos do Arduino com o mesmo nome*/

//Pinos dos Botões
#define SWITCH1_PIN                 PD4 //botão pause
#define SWITCH2_PIN                 PD3 //botão golo home,fio preto
#define SWITCH3_PIN                 PD2 //botão golo away,fio laranja
#define SWITCH4_PIN                 PD5 //botão reset

/*Struct tempo*/
typedef struct 
{
	uint8_t sec;
	uint8_t min;
}rtc_t;

rtc_t rtc;

//Funções declaradas aqui para ser permitida a sua utilização ao longo do programa
void setup(void);
void clear_display(uint8_t addr);
static void sendByte(uint8_t data);
void send(int bloco, uint8_t reg, uint8_t data);
void setLed(int addr, int row, int column, int state);
void setColumn(int addr, int col, uint8_t value);
void fill_blanks();
void home_goals_leds();
void away_goals_leds();
void minutes1();
void minutes2();
void seconds1();
void seconds2();
void resultado();
void init_ds3231(void);
void ds3231_SetDateTime(rtc_t *rtc);
void ds3231_GetDateTime(rtc_t *rtc);
uint8_t dec2bcd(char num);
uint8_t bcd2dec(char num);
int reset(int i);

/*Vetor para acompanhar o estado dos leds*/
uint64_t status[64];

/*Vetor para transmitir dados para o dispositivo*/
uint16_t spidata[16];

/*Variáveis dos golos*/
int r3=-1,r2=-1,r1=-1, r4=-1, home = 0, away = 0;
uint8_t EEMEM homee=0, awaye = 0;

/*Variáveis para o estado dos botões*/
uint8_t sw1,sw1_prev, sw2, sw2_prev, sw3, sw3_prev, sw4, sw4_prev, parado=0;

/*Variáveis para os minutos e segundos*/
int s=0,s1=0,s2=0,m=0,m1=0,m2=0,sp=0,mp=0;
uint8_t EEMEM se=0, me=0,spe=0,mpe=0;

/*Função setup inicaliza os botões e a matriz de LEDs*/
void setup(void)
{
DDRB |= (CLK | LOAD | DIN); // CLK, LOAD e DIN são outputs

PORTB |= (LOAD); // set LOAD HIGH 
PORTB &= ~(CLK | DIN); // set CLK e DIN LOW

DDRD &= ~(1 << SWITCH1_PIN) | (1 << SWITCH2_PIN) | (1 << SWITCH3_PIN) | (1 << SWITCH4_PIN); // Os botôes são input
PORTD |= (1 << SWITCH1_PIN) | (1 << SWITCH2_PIN) | (1 << SWITCH3_PIN) | (1 << SWITCH4_PIN); // set HIGH

//inicializa o vetor estado a zeros
for(int i=0;i<64;i++) status[i]=0x00;

//Inicializações para a matriz
for(uint8_t bloco = 0; bloco < BLOCOS; bloco++){
  send(bloco, SHUTDOWN, 1);             // Shutdown dos LEDs
  send(bloco, DISP_TEST, 0);            // Testa os LEDs
  send(bloco, DEC_MODE, 0);             // Estabelece o modo 
  send(bloco, INTENSITY, 13);           // Estabelece a luminosidade dos LEDs (1 a 15)
  send(bloco, SCAN_LIMIT, 7);           // Estabelece o limite de LEDs que são lidos em cada bloco
  clear_display(bloco);                 // Limpa o Display
}
}

/*Função para iniciar o RTC DS3231*/
void init_ds3231(void)
{
i2c_init();                             // Começa o módulo i2c
i2c_start(DS3231_WriteMode_U8);         // Começa a comunicação i2c e estabelece a conexão com o RTC (Write Mode) 
i2c_write(DS3231_REG_CONTROL);          // Seleciona o endereço do ControlRegister 
i2c_write(0x00);                        // Escreve 0x00 no Control register para desativar SQW-Out
i2c_stop();                             // Interrompe a comunicação i2c após o RTC estar inicializado
}

/*Função que dá reset ao tempo do RTC com os valores 
da struct rtc */
void ds3231_SetDateTime(rtc_t *rtc)
{
i2c_start(DS3231_WriteMode_U8);         // Começa a comunicação i2c e estabelece a conexão com o RTC (Write Mode)        
i2c_write(DS3231_REG_Seconds);          // Escreve o registo para aceder aos segundos      
i2c_write(rtc->sec);                    // Escreve os segundos pretendidos no registo
i2c_stop();                             // Interrompe a comunicação i2c

i2c_start(DS3231_WriteMode_U8);         // Começa a comunicação i2c e estabelece a conexão com o RTC (Write Mode) 
i2c_write(DS3231_REG_Minutes);          // Escreve o registo para aceder aos minutos
i2c_write(rtc->min);                    // Escreve os minutos pretendidos no registo
i2c_stop();                             // Interrompe a comunicação i2c
}

/*Função que lê os segundos atuais do RTC
e atualiza esse valor na variável s */
void readsec(rtc_t *rtc)
{
i2c_start(DS3231_WriteMode_U8);         // Começa a comunicação i2c e estabelece a conexão com o RTC (Write Mode) 
i2c_write(DS3231_REG_Seconds);          // Escreve o registo para aceder aos segundos
i2c_stop();                             // Interrompe a comunicação i2c                   

i2c_start(DS3231_ReadMode_U8);          // Começa a comunicação i2c e estabelece a conexão com o RTC (Read Mode)
rtc->sec = i2c_readNak();               // Lê os segundos
i2c_stop();                             // Interrompe a comunicação i2c 

s=rtc->sec;                             // Atualiza a variável s
}

/*Função que lê os minutos atuais do RTC
e atualiza esse valor na variável m */
void readmin(rtc_t *rtc)
{
i2c_start(DS3231_WriteMode_U8);         // Começa a comunicação i2c e estabelece a conexão com o RTC (Write Mode)
i2c_write(DS3231_REG_Minutes);          // Escreve o registo para aceder aos minutos
i2c_stop();                             // Interrompe a comunicação i2c                         

i2c_start(DS3231_ReadMode_U8);          // Começa a comunicação i2c e estabelece a conexão com o RTC (Read Mode)
rtc->min = i2c_readNak();               // Lê os minutos
i2c_stop();                             // Interrompe a comunicação i2c 

m=rtc->min;                             // Atualiza a variável m
}

/*Função que converte um número decimal (DEC) para binário (BCD)*/
uint8_t dec2bcd(char num)
{
return ((num/10 * 16) + (num % 10));
}

/*Função que converte um número em binário (BCD) para decimal (DEC)*/
uint8_t bcd2dec(char num)
{
return ((num/16 * 10) + (num % 16));
}

/*Desliga todos os LEDs da matriz antes do programa começar*/
void clear_display(uint8_t addr)
{
int offset;
offset=addr*8;

for(int i=0;i<8;i++) {
  status[offset+i]=0;
  send(addr, i+1,status[offset+i]);
}
}

/*Funçao sendByte tem como função enviar um byte para DIN
sempre que o clock ativa*/
static void sendByte(uint8_t data)
{
for(int j = 7; j >= 0; j--){
  PORTB &= ~CLK;                            // Rising edge do CLock
    
  if(data & (1 << j)) PORTB |= DIN;         // Se houver dados para enviar através de DIN envia
  else PORTB &= ~DIN;                       
    
  PORTB |= CLK;                             // Falling edge do Clock
}
}

/*Envia um comando único para a matriz*/
void send(int bloco, uint8_t reg, uint8_t data)
{
//Cria um vetor com os dados para serem enviados
int offset=bloco*2;
int maxbytes=BLOCOS*2;

for(int i=0;i<maxbytes;i++) spidata[i]=(uint8_t)0;

//Coloca os dados no vetor
spidata[offset+1]=reg;
spidata[offset]=data;

PORTB &= ~LOAD;

for(int i=maxbytes;i>0;i--) sendByte(spidata[i-1]);

PORTB |= LOAD;
}

/*Liga um determinado LED*/
void setLed(int addr, int row, int column, int state) 
{
int offset;
uint8_t val=0x00;

offset=addr*8;
val=0b10000000 >> column;
if(state==1)
  status[offset+row]=status[offset+row]|val;
else {
  val=~val;
  status[offset+row]=status[offset+row]&val;
}

send(addr, row+1,status[offset+row]);
}

/*Liga determinados LEDs de uma coluna*/
void setColumn(int addr, int col, uint8_t value) 
{
uint8_t val;

for(int row=0;row<8;row++) {
  val=value >> (7-row);
  val=val & 0x01;
  setLed(addr,row,col,val);
}
}

/*Liga os LEDs que fazem a separação entre os golos, 
os minutos dos segundos e o resultado do tempo*/
void fill_blanks()
{
setColumn(0,2,0B00010000);
setColumn(0,1,0B00010000);
setColumn(1,2,0B11111111);
setColumn(2,0,0B00100100);
}

/*LEDs para o número de golos da equipa caseira*/
void home_goals_leds()
{
if(home==0){
  setColumn(0,7,0B11111111);
  setColumn(0,6,0B10000001);
  setColumn(0,5,0B10000001);
  setColumn(0,4,0B11111111);
}
else if(home==1){
  setColumn(0,7,0B00000100);
  setColumn(0,6,0B10000010);
  setColumn(0,5,0B11111111);
  setColumn(0,4,0B10000000);
}
else if(home==2){
  setColumn(0,7,0B11110001);
  setColumn(0,6,0B10010001);
  setColumn(0,5,0B10010001);
  setColumn(0,4,0B10011111);
}
else if(home==3){
  setColumn(0,7,0B10011001);
  setColumn(0,6,0B10011001);
  setColumn(0,5,0B10011001);
  setColumn(0,4,0B11111111);
}
else if(home==4){
  setColumn(0,7,0B00001111);
  setColumn(0,6,0B00001000);
  setColumn(0,5,0B00001000);
  setColumn(0,4,0B11111111);
}
else if(home==5){
  setColumn(0,7,0B10001111);
  setColumn(0,6,0B10001001);
  setColumn(0,5,0B10001001);
  setColumn(0,4,0B11111001);
}
else if(home==6){
  setColumn(0,7,0B11111111);
  setColumn(0,6,0B10010001);
  setColumn(0,5,0B10010001);
  setColumn(0,4,0B11110001);
}
else if(home==7){
  setColumn(0,7,0B00000001);
  setColumn(0,6,0B00000001);
  setColumn(0,5,0B00000001);
  setColumn(0,4,0B11111111);
}
else if(home==8){
  setColumn(0,7,0B11111111);
  setColumn(0,6,0B10011001);
  setColumn(0,5,0B10011001);
  setColumn(0,4,0B11111111);
}
else if(home>=9){
  setColumn(0,7,0B00001111);
  setColumn(0,6,0B00001001);
  setColumn(0,5,0B00001001);
  setColumn(0,4,0B11111111);
}
}

/*LEDs para o número de golos da equipa visitante*/
void away_goals_leds()
{
if (away==0){
  setColumn(1,7,0B11111111);
  setColumn(1,6,0B10000001);
  setColumn(1,5,0B10000001);
  setColumn(1,4,0B11111111);
}
else if(away==1){
  setColumn(1,7,0B00000100);
  setColumn(1,6,0B10000010);
  setColumn(1,5,0B11111111);
  setColumn(1,4,0B10000000);
}
else if(away==2){
  setColumn(1,7,0B11110001);
  setColumn(1,6,0B10010001);
  setColumn(1,5,0B10010001);
  setColumn(1,4,0B10011111);
}
else if(away==3){
  setColumn(1,7,0B10011001);
  setColumn(1,6,0B10011001);
  setColumn(1,5,0B10011001);
  setColumn(1,4,0B11111111);
}
else if(away==4){
  setColumn(1,7,0B00001111);
  setColumn(1,6,0B00001000);
  setColumn(1,5,0B00001000);
  setColumn(1,4,0B11111111);
}
else if(away==5){
  setColumn(1,7,0B10001111);
  setColumn(1,6,0B10001001);
  setColumn(1,5,0B10001001);
  setColumn(1,4,0B11111001);
}
else if(away==6){
  setColumn(1,7,0B11111111);
  setColumn(1,6,0B10010001);
  setColumn(1,5,0B10010001);
  setColumn(1,4,0B11110001);
}
else if(away==7){
  setColumn(1,7,0B00000001);
  setColumn(1,6,0B00000001);
  setColumn(1,5,0B00000001);
  setColumn(1,4,0B11111111);
}
else if(away==8){
  setColumn(1,7,0B11111111);
  setColumn(1,6,0B10011001);
  setColumn(1,5,0B10011001);
  setColumn(1,4,0B11111111);
}
else if(away>=9){
  setColumn(1,7,0B00001111);
  setColumn(1,6,0B00001001);
  setColumn(1,5,0B00001001);
  setColumn(1,4,0B11111111);
}
}

/*LEDs para o número das dezenas dos minutos*/
void minutes1()
{
if (m1==0){
  setColumn(1,0,0B11111111);
  setColumn(2,7,0B10000001);
  setColumn(2,6,0B11111111);
}
else if(m1==1){
  setColumn(1,0,0B00000100);
  setColumn(2,7,0B00000010);
  setColumn(2,6,0B11111111);
}
else if(m1==2){
  setColumn(1,0,0B11110001);
  setColumn(2,7,0B10001001);
  setColumn(2,6,0B10000111);
}
else if(m1==3){
  setColumn(1,0,0B10011001);
  setColumn(2,7,0B10011001);
  setColumn(2,6,0B11111111);
}
else if(m1>=4){
  setColumn(1,0,0B00001111);
  setColumn(2,7,0B00001000);
  setColumn(2,6,0B11111111);
}
}

/*LEDs para o número das unidades dos minutos*/
void minutes2()
{
if(m2==0 || m1>=4){
  setColumn(2,4,0B11111111);
  setColumn(2,3,0B10000001);
  setColumn(2,2,0B11111111);
}
else if(m2==1){
  setColumn(2,4,0B00000100);
  setColumn(2,3,0B00000010);
  setColumn(2,2,0B11111111);
}
else if(m2==2){
  setColumn(2,4,0B11110001);
  setColumn(2,3,0B10001001);
  setColumn(2,2,0B10000111);
}
else if(m2==3){
  setColumn(2,4,0B10011001);
  setColumn(2,3,0B10011001);
  setColumn(2,2,0B11111111);
}
else if(m2==4){
  setColumn(2,4,0B00001111);
  setColumn(2,3,0B00001000);
  setColumn(2,2,0B11111111);
}
else if(m2==5){
  setColumn(2,4,0B10001111);
  setColumn(2,3,0B10001001);
  setColumn(2,2,0B11111001);
}
else if(m2==6){
  setColumn(2,4,0B11111111);
  setColumn(2,3,0B10010001);
  setColumn(2,2,0B11110001);
}
else if(m2==7){
  setColumn(2,4,0B00000001);
  setColumn(2,3,0B00000001);
  setColumn(2,2,0B11111111);
}
else if(m2==8){
  setColumn(2,4,0B11111111);
  setColumn(2,3,0B10011001);
  setColumn(2,2,0B11111111);
}
else if(m2==9){
  setColumn(2,4,0B00001111);
  setColumn(2,3,0B00001001);
  setColumn(2,2,0B11111111);
}  
}

/*LEDs para o número das dezenas dos segundos*/
void seconds2()
{
if (s2==0 || m1>=4){
  setColumn(3,2,0B11111111);
  setColumn(3,1,0B10000001);
  setColumn(3,0,0B11111111);
}
else if(s2==1){
  setColumn(3,2,0B00000100);
  setColumn(3,1,0B00000010);
  setColumn(3,0,0B11111111);
}
else if(s2==2){
  setColumn(3,2,0B11110001);
  setColumn(3,1,0B10001001);
  setColumn(3,0,0B10000111);
}
else if(s2==3){
  setColumn(3,2,0B10011001);
  setColumn(3,1,0B10011001);
  setColumn(3,0,0B11111111);
}
else if(s2==4){
  setColumn(3,2,0B00001111);
  setColumn(3,1,0B00001000);
  setColumn(3,0,0B11111111);
}
else if(s2==5){
  setColumn(3,2,0B10001111);
  setColumn(3,1,0B10001001);
  setColumn(3,0,0B11111001);
}
else if(s2==6){
  setColumn(3,2,0B11111111);
  setColumn(3,1,0B10010001);
  setColumn(3,0,0B11110001);
}
else if(s2==7){
  setColumn(3,2,0B00000001);
  setColumn(3,1,0B00000001);
  setColumn(3,0,0B11111111);
}
else if(s2==8){
  setColumn(3,2,0B11111111);
  setColumn(3,1,0B10011001);
  setColumn(3,0,0B11111111);
}
else if(s2==9){
  setColumn(3,2,0B00001111);
  setColumn(3,1,0B00001001);
  setColumn(3,0,0B11111111);
}
}

/*LEDs para o número das unidades dos segundos*/
void seconds1()
{
if(s1==0 || m1>=4){
  setColumn(3,6,0B11111111);
  setColumn(3,5,0B10000001);
  setColumn(3,4,0B11111111);
}
else if(s1==1){
  setColumn(3,6,0B00000100);
  setColumn(3,5,0B00000010);
  setColumn(3,4,0B11111111);
}
else if(s1==2){
  setColumn(3,6,0B11110001);
  setColumn(3,5,0B10001001);
  setColumn(3,4,0B10000111);
}
else if(s1==3){
  setColumn(3,6,0B10011001);
  setColumn(3,5,0B10011001);
  setColumn(3,4,0B11111111);
}
else if(s1==4){
  setColumn(3,6,0B00001111);
  setColumn(3,5,0B00001000);
  setColumn(3,4,0B11111111);
}
else if(s1==5){
  setColumn(3,6,0B10001111);
  setColumn(3,5,0B10001001);
  setColumn(3,4,0B11111001);
} 
}

/*Função que imprime o resultado do jogo*/
void resultado(int i)
{
/*Serial prints*/
for(int j=0;j<i;j++){
printf("%d",home);
printf("-");
printf("%d",away);
printf("  ");
printf("|");
printf("  ");
printf("%d",m1);
printf("%d",m2);
printf(":");
printf("%d",s1);
printf("%d",s2);
printf("\n");
}
}

/*Função para dar reset aos golos e ao tempo*/
/*A função reset deve estar no estado:
false -> se for pretendido que a EEPROM trabalhe normalmente guardando 
os valores das variáveis em memória não volátil.
true -> se for pretendida fazer reset na memória EEPROM, fazendo com
que o tempo volte a zero e o resultado 0-0. */
int reset(int i){
if(i==true){
  home=0;
  away=0;
  /*Guardar os golos na eeprom*/
  if(!(home==(eeprom_read_byte(&homee)))){
    eeprom_update_byte(&homee,(home));
  }
  if(!(away==(eeprom_read_byte(&awaye)))){
    eeprom_update_byte(&awaye,(away));
  }

  rtc.sec=0;
  rtc.min=0;
  ds3231_SetDateTime(&rtc);
  rtc.sec=(eeprom_read_byte(&se));
  rtc.min=(eeprom_read_byte(&me));
  /*Guardar o tempo na eeprom*/
  if(!(rtc.sec==(eeprom_read_byte(&se)))){
    eeprom_update_byte(&se,(rtc.sec));
  }
  if(!(rtc.min==(eeprom_read_byte(&me)))){
    eeprom_update_byte(&me,(rtc.min));
  }
return true;
} 
else{
  home=(eeprom_read_byte(&homee));
  away=(eeprom_read_byte(&awaye));
  /*Guardar os golos na eeprom*/
  if(!(home==(eeprom_read_byte(&homee)))){
    eeprom_update_byte(&homee,(home));
  }
  if(!(away==(eeprom_read_byte(&awaye)))){
    eeprom_update_byte(&awaye,(away));
  }

  rtc.sec=(eeprom_read_byte(&se));
  rtc.min=(eeprom_read_byte(&me));
  ds3231_SetDateTime(&rtc);

  return false;
}
}
/*Função main*/
int main(void)
{ 
//SETUP
setup();
init_ds3231();
printf_init(); //Inicia o Serial Print

reset(false);

printf("APITO INICIAL\n");

while(1)//LOOP
{
/*Liga os leds*/
home_goals_leds();
away_goals_leds();
fill_blanks();

/*Atualiza o número dos segundos e dos minutos e guardá-lo*/
readmin(&rtc);
readsec(&rtc);

/*Lê os botões dos golos para verificar se houve golos */
sw2_prev = sw2;
sw2 = !(PIND & (1 << SWITCH2_PIN));
r2++;

sw3_prev = sw3;
sw3 = !(PIND & (1 << SWITCH3_PIN));
r3++;

sw4_prev = sw4;
sw4 = !(PIND & (1 << SWITCH4_PIN));
r4++;

/*Botão de reset*/
if (!sw4_prev && sw4 && r4>=1) {
  reset(true);
  printf("APITO INICIAL\n");
}

/*Golo da equipa caseira*/
if (!sw2_prev && sw2 && r2>=1 && parado==0) {
  home++;
  home_goals_leds();
  printf("GOLO EQUIPA CASEIRA\n");
  resultado(1);
}

/*Golo da equipa visitante*/
if (!sw3_prev && sw3 && r3>=1 && parado==0) {
  away++;
  away_goals_leds();
  printf("GOLO EQUIPA VISITANTE\n");
  resultado(1);
}

/*Implementação de um botão de pausa no relógio 
parado == 0 significa que o relógio funciona normal.
parado == 1 significa que o relógio está parado. 
Enquanto o relógio se encontra no estado parado não pode haver golos.
O relógio só retoma a conta do tempo quando s1 for pressionado novamente*/

if(parado==0){
  jump:
  s1=(s-sp)/10;
  s2=(s-sp)-(s1*10);
  m1=(m-mp)/10;
  m2=(m-mp)-(m1*10);

  sw1_prev = sw1;
  sw1 = !(PIND & (1 << SWITCH1_PIN));
  r1++;
  if (!sw1_prev && sw1 && r1>=1) parado = 1;
}

if(parado==1){
  mp=m-((m1*10)+m2);
  sp=s-((s1*10)+s2);

  if(sp>=60){
    sp=60-sp;
    mp--;
  }
  sw1_prev = sw1;
  sw1 = !(PIND & (1 << SWITCH1_PIN));
  r1++;
  if (!sw1_prev && sw1 && r1>=1) {parado = 0;goto jump;}
}

/*Relógio*/
minutes1();
minutes2();
seconds1();
seconds2();

/*Guardar os golos na eeprom*/
if(!(home==(eeprom_read_byte(&homee)))){
  eeprom_update_byte(&homee,(home));
}
if(!(away==(eeprom_read_byte(&awaye)))){
  eeprom_update_byte(&awaye,(away));
}
/*Guardar o tempo na eeprom*/
if(!(rtc.sec==(eeprom_read_byte(&se)))){
  eeprom_update_byte(&se,(rtc.sec));
}
if(!(rtc.min==(eeprom_read_byte(&me)))){
  eeprom_update_byte(&me,(rtc.min));
}

/*FINAL DO JOGO*/
if(m1==4){
  printf("RESULTADO FINAL : %d | %d \n", home, away);
  break;
}
}
}