#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <sodium.h>


#define TOKEN_SIZE 128
#define PAYLOAD_SIZE crypto_secretbox_MACBYTES + TOKEN_SIZE
#define MSG_ASK "Can I get the solution to the challenge, please?" 
#define STATUS_BAD 0
#define STATUS_GOOD 1



/*message that will be sent*/
struct message {
int hacker_id; /* this is just the number part of the ID */ 
int status;
unsigned char nonce[crypto_secretbox_NONCEBYTES];
unsigned char payload[PAYLOAD_SIZE]; 
};



int main() {

    //initialize sodium
    if (sodium_init() < 0) {
        printf("Error initializing libsodium!\n");
        return 1;
    }

    //create socket
    int tcpsocket = socket(AF_INET, SOCK_STREAM, 0);
    if (tcpsocket < 0) {
        printf("Error creating socket!\n");
        return 1;
    }
    
   

    // Connect to the server
    struct sockaddr_in remote;
    // memset(&remote, '\0', sizeof(remote));
    remote.sin_addr.s_addr = inet_addr("192.168.1.77");
    remote.sin_family = AF_INET;
    remote.sin_port = htons(4000);

    if (connect(tcpsocket, (struct sockaddr *)&remote, sizeof(remote)) < 0) {
        printf("Error connecting to server!\n");
        return 1;
    }

    //while there is a connection
    
    struct message msg;
    /* SEND */
    msg.hacker_id = 56;
    randombytes(msg.nonce, sizeof msg.nonce); //generate random value
    char token[TOKEN_SIZE]; //buffer
    token[TOKEN_SIZE-1] = '\0';
    strcpy(token, MSG_ASK);
 
    FILE *f = fopen("/home/hackers/hacker56/key", "rb");
    if (f == NULL) {
        printf("Error opening key file!\n");
        return 1;
    }

    unsigned char key[crypto_secretbox_KEYBYTES];

    if (fread(key, 1, crypto_secretbox_KEYBYTES, f) < 1) {
        printf("Error reading key file!\n");
        return 1;
    }
 
    //encrypt message
    int encrypt = crypto_secretbox_easy(msg.payload, token, TOKEN_SIZE, msg.nonce, key);
    if (encrypt != 0) {
        printf("Error encrypting message!\n");
        return 1;
    }
      
    //read key into file
    int crypt = -1;
    unsigned char decrypted_token[TOKEN_SIZE];
    while(crypt != 0) {
    //send to server
    int sendval = send(tcpsocket, &msg, sizeof(struct message), 0);
    if (sendval < 0) {
        printf("Error sending message!\n");
        return 1;
    }
     
     struct message newstruct;
       /*RECEIVE*/
    int read = recv(tcpsocket, &newstruct, sizeof(struct message), 0);
    if (read < 0) {
        printf("Error receiving message!\n");
        return 1;
    }



    if (newstruct.status == STATUS_BAD) {
        printf("Received bad message:");
    }
    else if (newstruct.status == STATUS_GOOD) {
        printf("Received good message:");
    }
    else {
        printf("Error: unknown message status!\n");
    }

     crypt = crypto_secretbox_open_easy(decrypted_token, newstruct.payload, PAYLOAD_SIZE, newstruct.nonce, key);
        printf("Error: message integrity check failed!\n");
    }
     
     printf("pass!"); 
    // Hash the token using libsodium's generic hashing function
    unsigned char hash_output[crypto_generichash_BYTES];

    crypto_generichash(hash_output, sizeof hash_output, decrypted_token, TOKEN_SIZE, NULL, 0);

    // base64 encode the hash
    char encoded_hash[sodium_base64_ENCODED_LEN(sizeof hash_output,
     sodium_base64_VARIANT_ORIGINAL)];
    sodium_bin2base64(encoded_hash, sizeof encoded_hash,
      hash_output, sizeof hash_output,
        sodium_base64_VARIANT_ORIGINAL);

    printf("The encoded hash is: %s\n", encoded_hash);
    return 0;
    } //end of if status_good 
    
