import React from "react";
import { PureComponent } from "react";
import Dialog from "@material-ui/core/Dialog";
import DialogContent from "@material-ui/core/DialogContent";
import Tooltip from '@material-ui/core/Tooltip';


import FullCard from "./FullCard";
import SmallCard from "./SmallCard";
import {keywords} from "../Helpers/Constants";
import FittedText from "../Primitives/FittedText";

export class CardDisplay extends PureComponent {
  state = {
    popoverStyle: {}
  };

  handlePopoverOpen = (event) => {
    const {width, height} = this.props.windowDimensions;
    const rect = event.currentTarget.getBoundingClientRect();

    const style = {};
      if (rect.top < height / 2) {
          //Upper half
          if (rect.left < width / 2) {
              //Card is in Upper Left quadrant
              style["placement"] = "bottom-end";
          } else {
              //Card is in Upper Right quadrant
              style["placement"] = "bottom-begin";
          }
      } else {
          //Lower half
          if (rect.left < width / 2) {
              //Card is in Lower Left quadrant
              style["placement"] = "top-end";
          } else {
              //Card is in Lower Right quadrant
              style["placement"] = "top-begin";
          }
      }
    /*
    style["width"] = width / 5;
    style["height"] = (width / 5) * (88 / 63);
    style["display"] = "flex";
    style["textAlign"] = "left";
    //style["border"] = "2px solid red";


    if (rect.top < height / 2) {
      style["top"] = rect.height;
    } else {
      style["bottom"] = rect.height;
    }

    if (rect.left < width / 2) {
      style["left"] = rect.width;
    } else {
      style["right"] = rect.width;
      style["flexDirection"] = "row-reverse";
    }
    */
    this.setState({
      popoverStyle: style,
    });
  };

  handlePopoverClose = (event) => {
    this.setState({popoverStyle: {}});
  };

  render() {
    const {popoverStyle} = this.state;
    const { onClick, small, style, popoverDisabled, isDragging, withKeywords, dragHandleProps, ...rest } = this.props;
    return (
      <div
        {...dragHandleProps}
        style={{ width: "100%", height: "100%" }}

      >
        <Tooltip
            style ={{...popoverStyle}}
            title={

        !isDragging ? !popoverDisabled  ? (
            <div
                opacity={1}
                style ={{placement: "center"}}
            >
              <div
                  style={{
                  position: "relative",
                    justify: "center",
                    alignContent: "center",
                    //border: "2px blue solid"
                  }}
              >
                <FullCard scale={1.5} {...rest} opacity={1} square={false} preview={true} />
              </div>
              {withKeywords &&
              <div
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    //border: "2px green solid"
                  }}
              >
                {this.props.cardData.description &&
                Object.keys(keywords).map((keyword, i) => {
                  if (this.props.cardData.description.indexOf(keyword) !== -1 ||
                      (this.props.cardData.combat && this.props.cardData.combat.combatAbilities.indexOf(keyword) !== -1)) {
                    return (
                        <div
                            key={i}
                            style={{
                              color: "white",
                              backgroundColor: "black",
                              border: "1px white solid",
                              borderRadius: "5px",
                              whiteSpace: "pre-wrap",
                            }}
                        >
                          <FittedText
                              text={keyword + "\n" + keywords[keyword]}
                              windowDimensions={this.props.windowDimensions}
                          />
                        </div>
                    );
                  }
                  return <div key={i}/>;
                })}
              </div>
              }
            </div>
        ) : <div/> : <div/>
            }
        >
        <div onMouseEnter={this.handlePopoverOpen} onMouseLeave={this.handlePopoverClose} onClick={onClick? onClick : ()=>{}}
             style={style}>
          {small ? (
            <SmallCard {...rest} />
          ) : (
            <FullCard {...rest} />
          )}
        </div>

        </Tooltip>
      </div>
    );
  }
}

export default CardDisplay;
