package com.g4t1.client.controller;

public class ClientControllerTest {

    // create client tests
    // give no json return 400
    // give json with existing user return 400
    // give valid json return 201

    // put/update client tests
    // give null target id return 400
    // give non-exisiting id return 404
    // give null source return 204
    // give valid source return 200

    // get client tests
    // given null as id return 500
    // give non-exisiting id return 404
    // give existing user id return 200

    // delete client tests
    // given null as id return 500
    // give non-exisiting id return 404
    // give existing user id return 200

}
