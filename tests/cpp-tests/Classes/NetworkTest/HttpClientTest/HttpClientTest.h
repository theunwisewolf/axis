/****************************************************************************
 Copyright (c) 2017-2018 Xiamen Yaji Software Co., Ltd.

 https://axys1.github.io/

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/

#ifndef __HTTP_CLIENT_H__
#define __HTTP_CLIENT_H__

#include "cocos2d.h"
#include "extensions/cocos-ext.h"
#include "network/HttpClient.h"
#include "BaseTest.h"

DEFINE_TEST_SUITE(HttpClientTests);

class HttpClientTest : public TestCase
{
public:
    CREATE_FUNC(HttpClientTest);

    HttpClientTest();
    virtual ~HttpClientTest();

    // Menu Callbacks
    void onMenuGetTestClicked(axis::Ref* sender);
    void onMenuPostTestClicked(axis::Ref* sender);
    void onMenuPostBinaryTestClicked(axis::Ref* sender);
    void onMenuPutTestClicked(axis::Ref* sender);
    void onMenuDeleteTestClicked(axis::Ref* sender);

    // Http Response Callback
    void onHttpRequestCompleted(axis::network::HttpClient* sender, axis::network::HttpResponse* response);

    virtual std::string title() const override { return "Http Request Test"; }

private:
    axis::Label* _labelStatusCode;
};

class HttpClientClearRequestsTest : public TestCase
{
public:
    CREATE_FUNC(HttpClientClearRequestsTest);

    HttpClientClearRequestsTest();
    virtual ~HttpClientClearRequestsTest();

    // Menu Callbacks
    void onMenuCancelAllClicked(axis::Ref* sender);
    void onMenuCancelSomeClicked(axis::Ref* sender);

    // Http Response Callback
    void onHttpRequestCompleted(axis::network::HttpClient* sender, axis::network::HttpResponse* response);

    virtual std::string title() const override { return "Http Request Test"; }

private:
    int _totalExpectedRequests;
    int _totalProcessedRequests;
    axis::Label* _labelTrakingData;
    axis::Label* _labelStatusCode;
};

#endif  //__HTTPREQUESTHTTP_H
