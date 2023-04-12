/* eslint-disable */
import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
admin.initializeApp();

// Start writing functions
// https://firebase.google.com/docs/functions/typescript

export const getUser = functions.https.onRequest((request, response) => {
  admin.firestore()
    .doc("users/zFSzS1YVTFaADKNerzffXglMmna2").get()
    .then((snapshot) => {
      const data = snapshot.data();
      response.send(data);
    })
    .catch((error) => {
      console.log(error);
      response.status(500).send(error);
    });
});
