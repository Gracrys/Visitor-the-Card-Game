import React from "react";
import { PureComponent } from "react";
import Textfit from "react-textfit";
import Rectangle from "react-rectangle";
import VisibilitySensor from "react-visibility-sensor";

import {
  getCardColor,
  getIconColor,
  toKnowledgeString,
  toIconString
} from "../Helpers/Helpers";
import Fonts from "../Primitives/Fonts";
import "./css/FullCard.css";
import "../../css/Utils.css";

export default class FullCard extends PureComponent {
  state = { showDialog: false };

  render() {
    const {
      opacity,
      name,
      description,
      cost,
      type,
      knowledgeCost,
      borderColor,
      health,
      favor,
      loyalty,
      play
    } = this.props;

    return (
      <div>
        <Fonts />
        <Rectangle
          aspectRatio={[63, 88]}
          style={{
            opacity: opacity,
            backgroundColor: borderColor,
            overflow: "hidden"
          }}
        >
          <div
            className={"card-inner"+(play?"-play":"")}
            style={{ backgroundColor: getCardColor(knowledgeCost) }}
          >
            <div className="card-name">
             
                <span style={{ fontWeight: "500" }}>{cost}</span>
                <span
                  style={{
                    fontWeight: "500",
                    color: getIconColor(knowledgeCost)
                  }}
                >
                  {toIconString(toKnowledgeString(knowledgeCost))}
                </span>
                {" | " + name}
            </div>

            <div className="card-image">
              <VisibilitySensor>
                <img
                  src={process.env.PUBLIC_URL + "/img/" + type + ".png"}
                  style={{ maxWidth: "100%" }}
                  alt=""
                />
                {/*
              <Image
                src={[
                  process.env.PUBLIC_URL + "/img/" + name + ".jpg",
                  process.env.PUBLIC_URL + "/img/" + type + ".png"
                ]}
                style={{ maxWidth: "100%" }}
                decode={false}
              />
              */}
              </VisibilitySensor>
            </div>

            <div className="card-type">
                {type}
            </div>

            <div className="card-description" style={{ whiteSpace: "pre-wrap"}}>
                {description}
              {health ? "\nHealth:" + health : ""}
              {loyalty ? "\nLoyalty:" + loyalty : ""}
              {favor ? "\nFavor:" + favor : ""}
            </div>
          </div>
        </Rectangle>
      </div>
    );
  }
}