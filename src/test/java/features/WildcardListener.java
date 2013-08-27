/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package features;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WildcardListener implements DifferenceListener {

    private Pattern wildcard;

    public WildcardListener() {
        this.wildcard = Pattern.compile("^\\*$");
    }

    /**
     * On each difference this method is called by XMLUnit framework to determine
     * whether we accept the difference or ignore it. If any of the provided regular
     * expression match with xml xpath location at which difference found then ignore
     * the difference.
     *
     * @param difference contains information about differences.
     */
    public int differenceFound(Difference difference) {
        if (difference.getDescription() != "text value") {
            return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
        }

        String diffVal = difference.getControlNodeDetail().getValue();

        final Matcher m = this.wildcard.matcher(diffVal);

        if (m.find()) {
            return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL; /** ignore it, not a valid difference */
        }
        return DifferenceListener.RETURN_ACCEPT_DIFFERENCE; /** no objection, mark it as a valid difference */
    }

    /**
     * This method is required by the DifferenceListener interface.
     */
    public void skippedComparison(Node node, Node node1) {}
}


