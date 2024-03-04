package com.david.oramas.conversions.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@WebFilter("/part2/addRuleAndConditions")
@Slf4j
/*Class to make the first filter before sending the xml response*/
public class FilterXmlBeforeSendingIt implements Filter {

    private List<Integer> indexOfRepeatedBlocks = new ArrayList<>();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (request.getRequestURI().equals("/part2/addRuleAndConditions")) {
            //The content are read sequentially and not available after its read, so we cache before doing anything
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

            //Do the next filer -> we are not changing the request, the service will handle that,
            //so we can send it to the servlet, after doFilter call, we can modify the response.
            filterChain.doFilter(requestWrapper, responseWrapper);

            byte[] responseArray = responseWrapper.getContentAsByteArray();
            String responseStr = new String(responseArray, responseWrapper.getCharacterEncoding());
            log.info(responseStr);

            //String modifiedResponse = modifyServletResponseBeforeSendingIt(responseStr);
            String modifiedResponse;
            try {
                modifiedResponse = toXmlAndChangeBackToStringAfterReplacementOfTag(responseStr);
            } catch (SAXException | ParserConfigurationException | TransformerException e) {
                throw new RuntimeException(e);
            }
            log.info(responseStr);
            response.getOutputStream().write(modifiedResponse.getBytes(response.getCharacterEncoding()));
        }
        else{filterChain.doFilter(request, response);}
    }

    public String toXmlAndChangeBackToStringAfterReplacementOfTag(String response)
            throws IOException, SAXException, ParserConfigurationException, TransformerException {
        // Parse the XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(response));
        Document document = builder.parse(inputSource);

        // Find and update specific tags
        /*We could get the names of unnecessary nested blocks by doing a recursive function
        that checks in each node if the child node has the same name and save those,
        but there is two problems:
            1. Nested for loops for each call (depending on the node level we are checking)
            2. The code will be reaped (it can be fixed, using Function Interface)
        */
        //clear encapsulation for all ruleActionData elements
        NodeList ruleActionDataElementsList = document.getElementsByTagName("ruleActionData");
        this.clearExtraEncapsulationForNodesWithSameName(ruleActionDataElementsList);
        //clear encapsulation for all ruleData elements
        NodeList ruleDataElementsList = document.getElementsByTagName("ruleData");
        this.clearExtraEncapsulationForNodesWithSameName(ruleDataElementsList);
        //clear encapsulation for all conditionData elements
        Optional<NodeList> conditionDataElementsList = Optional.ofNullable(document.getElementsByTagName("conditionData"));
        conditionDataElementsList.ifPresent(this::clearExtraEncapsulationForNodesWithSameName);

        //because we are moving the elements on the xml tree, they are moved to the bottom
        //so the order in which we do the above matters -> move functionId and status to the end
        Element dataElement = document.getDocumentElement();
        this.moveToTheEndOfTheXMLTree(dataElement, "functionId");
        this.moveToTheEndOfTheXMLTree(dataElement, "status");

        // Serialize the modified XML back to a string
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        return writer.toString();
    }
    public void getIndexOfRepeatedBlocks (NodeList itemList){
        this.indexOfRepeatedBlocks.clear();
        for(int i=0; i<itemList.getLength();i++) {
            if (itemList.item(i).getFirstChild().getNodeName().equals(itemList.item(i).getNodeName()))
                this.indexOfRepeatedBlocks.add(i);
        }
    }
    public void clearExtraEncapsulationForNodesWithSameName(NodeList nodeToReFormat){
        //Find the index of all the blocks where the block contains the real values
        this.getIndexOfRepeatedBlocks(nodeToReFormat);
        //Move them into parentNode
        for (Integer index : this.indexOfRepeatedBlocks) {
            Element item = (Element) nodeToReFormat.item(index);
            NodeList children = item.getChildNodes();

            int appendedChildren=0;
            String nodeFirstChildRemoved = item.getFirstChild().getNodeName();

            while (children.getLength() > 0) {
                item.getParentNode().appendChild(children.item(0));
                appendedChildren++;
            }
            //need to save the string before it gets deleted from all lists
            String parentNodeRemoved = item.getParentNode().getNodeName();
            //and remove
            item.getParentNode().removeChild(item);
//            //update indexOfRepeatedBlocks, since now we have one less node
            if(!(parentNodeRemoved.equals("data")
                    && nodeFirstChildRemoved.equals("conditionData"))){
                for(int j = 1; j<this.indexOfRepeatedBlocks.size(); j++){
                    this.indexOfRepeatedBlocks.set(j,indexOfRepeatedBlocks.get(j)-1);
                }
            }
            else{
                for(int j = 1; j<this.indexOfRepeatedBlocks.size(); j++){
                    this.indexOfRepeatedBlocks.set(j,indexOfRepeatedBlocks.get(j)-(appendedChildren+1));
                }
            }
        }
    }

    public void moveToTheEndOfTheXMLTree(Element parentElement, String nodeToBeMove){
        NodeList elements = parentElement.getElementsByTagName(nodeToBeMove);
        if(elements.getLength()>0){
            Node childNode = elements.item(0);
            parentElement.removeChild(childNode);
            parentElement.appendChild(childNode);

        }
    }
}
